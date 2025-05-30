package com.example.closelineendjoinview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import androidx.core.graphics.toColorInt

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 6
val scGap : Float = 0.05f / parts
val rot1 : Float = 270f
val rot2 : Float = -90f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}
fun Canvas.drawCloseLineEndJoin(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 + (w / 2) * dsc(5), h / 2) {
        drawXY(0f, size) {
            rotate(rot1 * dsc(3))
            drawLine(size * (1 - dsc(0)), 0f, size, 0f, paint)
        }
        drawXY(0f, size) {
            drawLine(0f, 0f, size * dsc(1), -size * dsc(1), paint)
        }
        drawXY(size, 0f) {
            rotate(rot2 * dsc(4))
            drawLine(0f, 0f, 0f, -size * dsc(2), paint)
        }
    }
}

fun Canvas.drawCLEJNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawCloseLineEndJoin(scale, w, h, paint)
}
