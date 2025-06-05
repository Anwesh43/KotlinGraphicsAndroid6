package com.example.lineshiftjoindownview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import androidx.core.graphics.toColorInt
import android.content.Context
import android.app.Activity
import android.graphics.Color
import androidx.constraintlayout.widget.ConstraintSet.Motion

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 5
val scGap : Float = 0.05f / parts
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val delay : Long = 20
val rot1 : Float = 90f
val rot2 : Float = -180f
val backColor : Int = "#BDBDBD".toColorInt()

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawLineShiftJoinDown(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 + (h / 2) * dsc(4)) {
        drawXY(0f, 0f) {
            rotate(rot2 * dsc(3))
            drawLine(0f, 0f, 0f, -size * dsc(0), paint)
        }
        drawXY(0f, -size * (1 - dsc(2))) {
            rotate(rot1 * dsc(3))
            drawLine(0f, 0f, size * dsc(1), 0f, paint)
        }
    }
}

fun Canvas.drawLSJDNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawLineShiftJoinDown(scale, w, h, paint)
}

class LineShiftJoinDownView(ctx : Context) : View(ctx) {

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

    data class LSJDNode(var i : Int = 0, val state : State = State()) {

        private var next : LSJDNode? = null
        private var prev : LSJDNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = LSJDNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawLSJDNode(i, state.scale, paint)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : LSJDNode {
            var curr : LSJDNode? = prev
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

    data class LineShiftJoinDown(var i : Int) {

        private var curr : LSJDNode = LSJDNode(0)
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

    data class Renderer(var view : LineShiftJoinDownView) {

        private val animator : Animator = Animator(view)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val lsjd : LineShiftJoinDown = LineShiftJoinDown(0)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            lsjd.draw(canvas, paint)
            animator.animate {
                lsjd.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            lsjd.startUpdating {
                animator.start()
            }
        }
    }
}
