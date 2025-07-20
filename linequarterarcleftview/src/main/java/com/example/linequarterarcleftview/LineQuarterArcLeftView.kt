package com.example.linequarterarcleftview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import androidx.core.graphics.toColorInt
import android.graphics.RectF
import android.content.Context
import android.app.Activity

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val rot : Float = 90f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
val sizeFactor : Float = 6.9f
val strokeFactor : Float = 90f
val parts : Int = 5
val scGap : Float = 0.04f / parts

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}
fun Canvas.drawLineQuarterArcLeft(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 - (w / 2) * dsc(4), h / 2) {
        drawXY(-size, -h * 0.5f * (1 - dsc(0))) {
            var cumDeg : Float = 0f
            for (j in 0..1) {
                cumDeg += rot * dsc(j + 1)
                drawXY(0f, 0f) {
                    rotate(cumDeg)
                    drawLine(0f, 0f, 0f, -size, paint)
                }
            }
        }
        drawArc(RectF(-2 * size, -size, 0f, size), 0f, 90f * dsc(3), false, paint)
    }
}

fun Canvas.drawLQALNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawLineQuarterArcLeft(scale, w, h, paint)
}
