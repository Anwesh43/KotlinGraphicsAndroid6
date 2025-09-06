package com.example.linebentdropperpview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
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
val rot : Float = -180f
val deg : Float = 45f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
val sizeFactor : Float = 5.9f
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

fun Canvas.drawLineBentDropPerp(scale : Float, w : Float, h : Float, paint : Paint) {
    val size : Float = Math.min(w, h) / sizeFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2 + (w / 2) * dsc(4), h / 2) {
        drawLine(0f, 0f, size * dsc(0), 0f, paint)
        drawXY(size, 0f) {
            rotate(deg)
            drawLine(0f, 0f, -size * dsc(1), 0f, paint)
        }
        drawXY(0f, -h * 0.5f * (1 - dsc(2))) {
            rotate(rot * dsc(3))
            drawLine(0f, 0f, 0f, -size, paint)
        }
    }
}

fun Canvas.drawLBDPNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawLineBentDropPerp(scale, w, h, paint)
}

class LineBentDropPerpView(ctx : Context) : View(ctx) {

    override fun onDraw(canvas : Canvas) {

    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
}
