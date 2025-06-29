package com.example.concarclinedownview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.graphics.toColorInt
import android.app.Activity
import android.content.Context

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 7
val scGap : Float = 0.06f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
val rot : Float = 90f
val deg : Float = 45f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawConcArcLineDown(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    val r1 : Float = size * 0.5f
    val r2 : Float = size
    drawXY(w / 2 + (w / 2) * dsc(6), h / 2 + (h / 2) * dsc(6)) {
        rotate(rot * dsc(5))
        drawArc(RectF(-r1, -r1, r1, r1), -deg * 2, deg * dsc(0), false, paint)
        drawXY(0f, 0f) {
            rotate(-deg)
            drawXY(r1, 0f) {
                drawLine(0f, 0f, (r2 - r1) * dsc(1), 0f, paint)
            }
        }
        drawArc(RectF(-r2, -r2, r2, r2), -deg, deg * dsc(2), false, paint)
        drawXY(r2, 0f) {
            drawLine(-r2 * dsc(3), 0f, 0f, 0f, paint)
        }
        drawLine(0f, 0f, 0f, -r1 * dsc(4), paint)
    }
}

fun Canvas.drawCALDNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawConcArcLineDown(scale, w, h, paint)
}

class ConcArcLineDownView(ctx : Context) : View(ctx) {

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

    data class CALDNode(var i : Int = 0, val state : State = State()) {

        private var next : CALDNode? = null
        private var prev : CALDNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = CALDNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawCALDNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : CALDNode {
            var curr : CALDNode? = prev
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

    data class ConcArcLineDown(var i : Int = 0, val state : State = State()) {

        private var curr : CALDNode = CALDNode(0)
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

    data class Renderer(var view : ConcArcLineDownView) {

        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val cald : ConcArcLineDown = ConcArcLineDown(0)
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            cald.draw(canvas, paint)
            animator.animate {
                cald.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            cald.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity: Activity) : ConcArcLineDownView {
            val view : ConcArcLineDownView = ConcArcLineDownView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
