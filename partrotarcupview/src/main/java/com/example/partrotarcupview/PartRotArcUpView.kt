package com.example.partrotarcupview

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
val rot : Float = 90f
val parts : Int = 5
val scGap : Float = 0.04f / parts
val strokeFactor : Float = 90f
val backColor : Int = "#BDBDBD".toColorInt()
val delay : Long = 20
val sizeFactor : Float = 6.9f
