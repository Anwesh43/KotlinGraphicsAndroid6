package com.example.midlinerotupview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import androidx.core.graphics.toColorInt
import android.content.Context
import android.app.Activity
import androidx.constraintlayout.widget.ConstraintSet.Motion

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
val rot : Float = 60f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawMidLineRotUp(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 - (h / 2) * dsc(3)) {
        rotate(rot * dsc(2))
        for (j in 0..1) {
            val x1 : Float = -size * (1 - j) + size * 0.5f * dsc(1) * (1 - j) - size * 0.5f * dsc(1) * j
            val x2 : Float = -size * (1 - j) + size * (1 - j) * dsc(0)
            drawXY(0f, 0f) {
                rotate(rot * j)
                drawLine(x1, 0f, x2, 0f, paint)
            }
        }
    }
}

fun Canvas.drawMLRUNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.strokeCap = Paint.Cap.ROUND
    drawMidLineRotUp(scale, w, h, paint)
}

class MidLineRotUpView(ctx : Context) : View(ctx) {

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

    data class MLRUNode(var i : Int = 0, val state : State = State()) {

        private var next : MLRUNode? = null
        private var prev : MLRUNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = MLRUNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawMLRUNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : MLRUNode {
            var curr : MLRUNode? = prev
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
}