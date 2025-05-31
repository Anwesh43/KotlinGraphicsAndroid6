package com.example.triinsqrotrightview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.RectF
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val inSizeFactor : Float = 9.2f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
val rot : Float = 180f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int): Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawTriinSqRotRight(i : Int, scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    val dsk : (Int, Int, Int) -> Float = {i, j, k -> dsc(i).divideScale(j, k)}
    drawXY(w / 2, h / 2) {
        paint.color = backColor
        val inSize : Float = Math.min(w, h) / inSizeFactor
        for (j in 0..1) {
            drawXY(size / 2, -size / 2) {
                rotate(rot * dsc(2) * j)
                drawXY(inSize / 2, -inSize / 2) {
                    drawLine(0f, 0f, 0f, inSize * dsk(1, 0, 2), paint)
                }
                drawXY(inSize / 2, inSize / 2) {
                    drawLine(0f, 0f, -inSize * dsk(1, 1, 2), 0f, paint)
                }
            }
        }
        paint.color = colors[i].toColorInt()
        drawRect(RectF(0f, -size, size * dsc(0), 0f), paint)
    }
}

fun Canvas.drawTSRRNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawTriinSqRotRight(i, scale, w, h, paint)
}

class TriinSqRotRightView(ctx : Context) : View(ctx) {

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

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

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

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }
}