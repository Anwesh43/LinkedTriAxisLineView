package com.example.triaxislineview

import android.view.View
import android.view.MotionEvent
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Canvas
import android.content.Context
import android.app.Activity

val lines : Int = 3
val parts : Int = lines + 2
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
val lWidth : Float = 0.9f

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n
fun Float.sinify() : Float = Math.sin(this * Math.PI).toFloat()

fun Canvas.drawTriAxisLine(scale : Float, w : Float, h : Float , paint : Paint)  {
    val size : Float = Math.min(w, h) / lSizeFactor
    val lSize : Float = size / axisLineFactor
    val sf : Float = scale.sinify()
    val sf1 : Float = sf.divideScale(0, parts)
    val sf2 : Float = sf.divideScale(1, parts)
    val lGap : Float = (lWidth * size) / lines
    val offset : Float = (size - lGap * lines) / 2
    val yOffset : Float = (size - lSize) / 2
    save()
    translate(w / 2, h / 2)
    for (j in 0..1) {
        save()
        rotate(-90f * sf2)
        drawLine(0f, 0f, size * sf1, 0f, paint)
        restore()
    }
    save()
    translate(offset, -lSize / 4)
    for (j in 0..(lines - 1)) {
        val si : Int = j % 2
        val sj : Float = 1f - 2 * si
        val sfj : Float = sf.divideScale(2 + j, parts)
        save()
        translate(lGap * j, -yOffset)
        drawLine(0f, 0f, lGap * sfj, -lSize * sfj * sj, paint)
        restore()
    }
    restore()
    restore()
}

fun Canvas.drawTALNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i]
    paint.strokeCap = Paint.Cap.ROUND
    paint.strokeWidth = Math.min(w, h) / strokeFactor
    drawTriAxisLine(scale, w, h, paint)
}
