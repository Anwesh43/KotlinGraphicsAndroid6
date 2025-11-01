package com.example.orbitalballscollapseview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.graphics.toColorInt

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 4
val scGap : Float = 0.04f / parts
val sizeFactor : Float = 90f
val backColor : Int ="#BDBDBD".toColorInt()
val rot : Float = 180f
val delay : Long = 20
val sweep : Float = 360f
val balls : Int = 6
val ballRFactor : Float = 5.2f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawOrbitalBallsCollapse(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = (Math.min(w, h) / sizeFactor)
    val ballR : Float = (size / ballRFactor)
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    val uSize : Float = size * (1 - dsc(3).divideScale(1, 2))
    val uBallR : Float = ballR * (1 - dsc(3).divideScale(0, 2))
    drawXY(w / 2, h / 2) {
        rotate(rot * dsc(3))
        drawArc(RectF(-uSize / 2, -uSize / 2, uSize / 2, uSize / 2), 0f, sweep * dsc(0), true, paint)
        for (j in 0..(balls - 1)) {
            drawXY(0f, 0f) {
                rotate((360f / ballR) * j)
                drawXY(0f, -2 * size * (1 - dsc(3).divideScale(0, 2))) {
                    drawArc(RectF(-uBallR, -uBallR, uBallR, uBallR), 0f, sweep * dsc(1), true, paint)
                }
            }
        }
    }
}

fun Canvas.drawOBCNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    drawOrbitalBallsCollapse(scale, w, h, paint)
}

class OrbitalBallsCollapseView(ctx : Context) : View(ctx) {

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

    data class OBCNode(var i : Int = 0, val state : State = State()) {

        private var next : OBCNode? = null
        private var prev : OBCNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = OBCNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawOBCNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : OBCNode {
            var curr : OBCNode? = prev
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

    data class OrbitalBallsCollapse(var i : Int) {

        private var curr : OBCNode = OBCNode(0)
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
}