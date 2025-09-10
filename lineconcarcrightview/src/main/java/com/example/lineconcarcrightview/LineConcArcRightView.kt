package com.example.lineconcarcrightview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.toColorInt
import android.graphics.Canvas

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 6
val scGap : Float = 0.05f / parts
val rot : Float = 90f
val sizeFactor : Float = 5.9f
val strokeFactor : Float = 90f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
