package com.example.trilinearcbisectview

import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import androidx.core.graphics.toColorInt
import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.app.Activity

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 4
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
val rot : Float = 45f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawTriLineArcBisect(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    val dsk : (Int, Int, Int) -> Float =  { i: Int, i1: Int, i2: Int ->
        dsc(i).divideScale(i1 , i2)
    }

    drawXY(w / 2 - (w / 2) * dsc(3), h / 2) {
        drawXY(0f, 0f) {
            rotate(-rot * dsc(2))
            drawLine(0f, 0f, -size * 0.5f * dsk(0, 0, 2), size * 0.5f * dsk(0, 0, 2), paint)
            drawXY(-size / 2, size / 2) {
                drawLine(0f, 0f, -size * 0.5f * dsk(0, 1, 2), -size * 0.5f * dsk(0, 1, 2), paint)
            }
        }
        drawXY(0f, 0f) {
            rotate(rot * dsc(2))
            drawArc(RectF(-size, -size / 2, 0f, size / 2), 180f, 180f * dsc(1), false, paint)
        }
    }
}

fun Canvas.drawTLABNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawTriLineArcBisect(scale, w, h, paint)
}

class TriLineArcBisectView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir === 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class TLABNode(var i : Int = 0, val state : State = State()) {

        private var next : TLABNode? = null
        private var prev : TLABNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = TLABNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawTLABNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : TLABNode {
            var curr : TLABNode? = prev
            if (dir === 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class TriLineArcBisect(var i : Int) {

        private var curr : TLABNode = TLABNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : TriLineArcBisectView) {

        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val animator : Animator = Animator(view)
        private val tlab : TriLineArcBisect = TriLineArcBisect(0)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            tlab.draw(canvas, paint)
            animator.animate {
                tlab.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            tlab.startUpdating {
                animator.start()
            }
        }
    }
}