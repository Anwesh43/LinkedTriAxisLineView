package com.example.triaxislineview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.content.Context
import android.app.Activity

val parts : Int = 5
val lSizeFactor : Float = 3f
val strokeFactor : Float = 90f
val axisLineFactor : Float = 5.8f
val delay : Long = 20
val scGap : Float = 0.02f / parts
val colors : Array<Int> = arrayOf(
    "#F44336",
    "#009688",
    "#673AB7",
    "#4CAF50",
    "#FF9800"
).map {
    Color.parseColor(it)
}.toTypedArray()
val backColor : Int = Color.parseColor("#BDBDBD")

