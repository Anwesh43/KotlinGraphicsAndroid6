package com.example.slantarclinerightview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.RectF
import android.app.Activity
import android.content.Context
import androidx.core.graphics.toColorInt

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 4
val scGap : Float = 0.04f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
val rot : Float = 135f
val deg : Float = -45f
val sizeFactor : Float = 5.9f
val strokeFactor : Float = 90f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y: Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawSlantArcLineRight(scale : Float, w : Float, h : Float, paint : Paint) {
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    val size : Float = Math.min(w, h) / sizeFactor
    drawXY(w / 2 + size * (1 - dsc(2)) + w * 0.5f * dsc(4), h / 2) {
        rotate(deg + rot * dsc(3))
        drawLine(0f, 0f, 0f, -size * dsc(0), paint)
        drawArc(RectF(-size / 2, -size, size / 2, 0f), -90f, 180f * dsc(1), false, paint)
    }
}

fun Canvas.drawSALRNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = Color.parseColor(colors[i])
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawSlantArcLineRight(scale, w, h, paint)
}
