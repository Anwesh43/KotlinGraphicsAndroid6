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
