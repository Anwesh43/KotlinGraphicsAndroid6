package com.example.linemultiforkrotview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import androidx.core.graphics.toColorInt

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val rot : Float = 180f
val deg : Float = 60f
val backColor : Int = "#BDBDBD".toColorInt()
val sizeFactor : Float = 6.9f
val strokeFactor : Float = 90f
val parts : Int = 4
val scGap : Float = 0.04f / parts
