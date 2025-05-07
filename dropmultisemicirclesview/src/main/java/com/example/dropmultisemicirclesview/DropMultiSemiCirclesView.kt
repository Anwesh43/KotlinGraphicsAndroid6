package com.example.dropmultisemicirclesview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Color
import android.app.Activity
import android.content.Context

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 3
val scGap : Float = 0.03f / parts
val arcs : Int = 4
val rot : Float = -180f
val delay : Long = 20
val sizeFactor : Float = 6.9f
val strokeFactor : Float = 90f
val backColor : Int = Color.parseColor("#BDBDBD")
