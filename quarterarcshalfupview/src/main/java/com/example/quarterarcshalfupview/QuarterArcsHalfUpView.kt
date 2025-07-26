package com.example.quarterarcshalfupview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.graphics.toColorInt
import android.content.Context
import android.app.Activity

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 5
val scGap : Float = 0.04f / parts
val rot : Float = 90f
val deg : Float = -180f
val backColor : Int = "#BDBDBD".toColorInt()
val delay : Long = 20
val sizeFactor : Float = 5.9f
val strokeFactor : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawQuarterArcHalfUp(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 - (h / 2) * dsc(4)) {
        rotate(rot * dsc(3))
        for (j in 0..1) {
            val shift : Float = size * 0.5f * (1 - j) * Math.floor(dsc(j).toDouble()).toFloat()
            drawXY(-size * (1 - j) + shift, 0f) {
                rotate(deg * (1 - j) * dsc(2))
                var startDeg : Float = -90f * (1 + j)
                drawArc(RectF(-size / 2 - shift, -size / 2, size / 2 - shift, size / 2), startDeg, rot * dsc(j), false, paint)
            }
        }
    }
}

fun Canvas.drawQAHUNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawQuarterArcHalfUp(scale, w, h, paint)
}

class QuarterArcsHalfUpView(ctx : Context) : View(ctx) {

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

    data class Animator(val view : View, var animated : Boolean = false) {

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

    data class QAHUNode(var i : Int = 0, val state : State = State()) {

        private var next : QAHUNode? = null
        private var prev : QAHUNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = QAHUNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawQAHUNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : QAHUNode {
            var curr : QAHUNode? = prev
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

    data class QuarterArcsHalfUp(var i : Int = 0, val state : State = State()) {

        private var curr : QAHUNode = QAHUNode(0)
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

    data class Renderer(var view : QuarterArcsHalfUpView) {

        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val qahu : QuarterArcsHalfUp = QuarterArcsHalfUp(0)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            qahu.draw(canvas, paint)
            animator.animate {
                qahu.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            qahu.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity: Activity) : QuarterArcsHalfUpView {
            val view : QuarterArcsHalfUpView = QuarterArcsHalfUpView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
