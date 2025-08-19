package com.example.bentlinetofshapeview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import androidx.core.graphics.toColorInt
import android.graphics.Canvas
import android.app.Activity
import android.content.Context

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
val rot : Float = 45f
val lineSizeFactor : Float = 0.25f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()
