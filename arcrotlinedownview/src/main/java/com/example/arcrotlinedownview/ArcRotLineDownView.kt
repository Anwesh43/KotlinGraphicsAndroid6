package com.example.arcrotlinedownview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.app.Activity
import android.content.Context
import android.graphics.RectF
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
val strokeFactor : Float = 90f
val sizeFactor : Float = 5.9f
val delay : Long = 20
val backColor : Int = "#bdbdbd".toColorInt()
val rot : Float = 180f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawArcRotLineDown(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2 + (h / 2) * dsc(5)) {
        drawXY(0f, -h *0.5f * (1 - dsc(0))) {
            rotate(rot * dsc(1))
            drawLine(0f, 0f, 0f, -size, paint)
        }
        drawXY(0f, 0f) {
            rotate(rot * dsc(3))
            drawArc(RectF(-size / 2, -size, size / 2, 0f), 90f, 180f * dsc(2), false, paint)
        }
        drawArc(RectF(-size / 2, 0f, size / 2, size), 90f, 180f * dsc(4), false, paint)
    }
}

fun Canvas.drawARLDNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    paint.style = Paint.Style.STROKE
    drawArcRotLineDown(scale, w, h, paint)
}

class ArcRotLineDownView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {
        val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        canvas.drawARLDNode(0, 0.5f, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}