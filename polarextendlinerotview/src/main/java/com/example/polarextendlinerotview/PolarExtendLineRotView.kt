package com.example.polarextendlinerotview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import androidx.core.graphics.toColorInt
import android.graphics.Canvas

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val lines : Int = 6
val parts : Int = lines + 2
val scGap : Float = 0.06f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
val rot : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawPolarExtendLineRot(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    val deg : Float = 180f / (lines - 1)
    drawXY(w / 2, h / 2 - (h / 2) * dsc(lines + 1)) {
        rotate(rot * dsc(lines))
        for (j in 0..(lines - 1)) {
            drawXY(0f, 0f) {
                rotate(deg * j)
                drawLine(0f, 0f, 0f, size * dsc(j), paint)
            }
        }
    }
}

fun Canvas.drawPELRNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawPolarExtendLineRot(scale, w, h, paint)
}

class PolarExtendLineRotView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
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

    data class PELRNode(var i : Int = 0, val state : State = State()) {

        private var next : PELRNode? = null
        private var prev : PELRNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = PELRNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawPELRNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : PELRNode {
            var curr : PELRNode? = prev
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

    data class PolarExtendRotLine(var i : Int) {

        private var curr : PELRNode = PELRNode(0)
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

    data class Renderer(var view : PolarExtendLineRotView) {

        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val pelr : PolarExtendRotLine = PolarExtendRotLine(0)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            pelr.draw(canvas, paint)
            animator.animate {
                pelr.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            pelr.startUpdating {
                animator.start()
            }
        }
    }
}