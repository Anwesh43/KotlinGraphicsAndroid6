package com.example.quarterarcshalfupview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import androidx.core.graphics.toColorInt
import android.content.Context
import android.app.Activity

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 5
val scGap : Float = 0.04f / parts
val rot : Float = 90f
val deg : Float = -180f
val backColor : Int = "#BDBDBD".toColorInt()
val delay : Long = 20
val sizeFactor : Float = 5.9f
val strokeFactor : Float = 90f