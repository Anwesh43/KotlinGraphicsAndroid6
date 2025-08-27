package com.example.linejoinarcdownview

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
val arcDeg : Float = 45f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawLineJoinArcDown(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 , h * 0.5f * (dsc(0) + dsc(4))) {
        rotate(rot * (dsc(1) + dsc(3)))
        for (j in 0..1) {
            drawXY(0f, 0f) {
                rotate(arcDeg * j * dsc(2))
                drawLine(0f, 0f, 0f, -size, paint)
            }
        }
        drawArc(RectF(-size, -size, size, size), 0f, arcDeg * dsc(2), false, paint)
    }
}

fun Canvas.drawLJADNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawLineJoinArcDown(scale, w, h, paint)
}
