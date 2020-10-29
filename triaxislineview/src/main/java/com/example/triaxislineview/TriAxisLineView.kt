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

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()
