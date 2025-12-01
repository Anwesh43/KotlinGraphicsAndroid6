package com.example.linedividearcrightview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Canvas
import androidx.core.graphics.toColorInt

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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawLineDivideArcRight(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 + (w / 2) * dsc(6), h / 2) {
        rotate(rot * dsc(5))
        drawXY(-size * dsc(4), 0f) {
            drawLine(0f, 0f, 0f, -size * dsc(0), paint)
            drawArc(RectF(-size, -size, size, size), -90f, 90f * dsc(1), false, paint)
            drawXY(size, 0f) {
                drawArc(RectF(0f, -size, 2 * size, size), 180f, 90f * dsc(2), false, paint)
            }
            drawXY(0f, -size) {
                drawLine(0f, 0f, 0f, size * dsc(3), paint)
            }
        }
    }
}

fun Canvas.drawLDARNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawLineDivideArcRight(scale, w, h, paint)
}
