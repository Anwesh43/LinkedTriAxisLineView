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

class TriAxisLineView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f ){

        fun update(cb  :(Float) -> Unit) {
            scale += scGap * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1f - 2 * prevScale
                cb()
            }
        }
    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(delay)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start() {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class TALNode(var i : Int, val state : State = State()) {

        private var next : TALNode? = null
        private var prev : TALNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = TALNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawTALNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : TALNode {
            var curr : TALNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class TriAxisLine(var i : Int) {

        private var curr : TALNode = TALNode(0)
        private var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Float) -> Unit) {
            curr.update {
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(it)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : TriAxisLineView) {

        private val animator : Animator = Animator(view)
        private val tal : TriAxisLine = TriAxisLine(0)
        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            tal.draw(canvas, paint)
            animator.animate {
                tal.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            tal.startUpdating {
                animator.start()
            }
        }
    }

    companion object {

        fun create(activity : Activity) : TriAxisLineView {
            val view : TriAxisLineView = TriAxisLineView(activity)
            activity.setContentView(view)
            return view
        }
    }
}