package com.example.arcjoinlineupview

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
val parts : Int = 5
val scGap : Float = 0.04f / parts
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

fun Canvas.drawArcJoinLineUp(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 - (h / 2) * dsc(4)) {
        rotate(rot * dsc(3))
        drawArc(RectF(-size, -size / 2, 0f, size / 2), 0f, 180f * dsc(0), false, paint)
        drawXY(-size, 0f) {
            drawLine(0f, 0f, size * dsc(1), -size * dsc(1), paint)
        }
        drawXY(0f, -size) {
            drawLine(0f, 0f, 0f, size * dsc(2), paint)
        }
    }
}

fun Canvas.drawAJLUNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawArcJoinLineUp(scale, w, h, paint)
}

class ArcJoinLineUpView(ctx : Context) : View(ctx) {

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

    data class AJLUNode(var i : Int, val state : State = State()) {

        private var next : AJLUNode? = null
        private var prev : AJLUNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = AJLUNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawAJLUNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : AJLUNode {
            var curr : AJLUNode? = prev
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

    data class ArcJoinLineUp(var i : Int) {

        private var curr : AJLUNode = AJLUNode(0)
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

    data class Renderer(var view : ArcJoinLineUpView) {

        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val ajlu : ArcJoinLineUp = ArcJoinLineUp(0)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            ajlu.draw(canvas, paint)
            animator.animate {
                ajlu.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            ajlu.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity : Activity) : ArcJoinLineUpView {
            val view : ArcJoinLineUpView = ArcJoinLineUpView(activity)
            activity.setContentView(view)
            return view
        }
    }
}