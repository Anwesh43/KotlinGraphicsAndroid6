package com.example.altbarseparaterotview

import android.view.View
import android.view.MotionEvent
import android.app.Activity
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import androidx.core.graphics.toColorInt
import android.graphics.RectF

val colors : Array<String> = arrayOf(
    "#1A237E",
    "#EF5350",
    "#AA00FF",
    "#C51162",
    "#00C853"
)
val parts : Int = 4
val scGap : Float = 0.04f / parts
val hFactor : Float = 12.9f
val wFactor : Float = 5.9f
val rot : Float = 90f
val delay : Long = 20
val backColor : Int = "#BDBDBD".toColorInt()

fun Int.inverse() : Float = 1f / this
fun Float.maxScale(i : Int, n : Int) : Float = Math.max(0f, this - i * n.inverse())
fun Float.divideScale(i : Int, n : Int) : Float = Math.min(n.inverse(), maxScale(i, n)) * n

fun Canvas.drawXY(x : Float, y : Float, cb : () -> Unit) {
    save()
    translate(x, y)
    cb()
    restore()
}

fun Canvas.drawAltBarSeparateRot(scale : Float, w : Float, h : Float, paint : Paint) {
    val barW : Float = Math.min(w, h) / wFactor
    val barH : Float = Math.min(w, h) / hFactor
    val dsc : (Int) -> Float = {
        scale.divideScale(it, parts)
    }
    drawXY(w / 2, h / 2) {
        for (j in 0..1) {
            drawXY(0f, 0f) {
                scale(1f - 2 * j, 1f)
                drawXY((w / 2 - barW) * dsc(1), (h * 0.5f * dsc(3)) * (1f - 2 * j)) {
                    rotate(rot * dsc(2) * (1f - 2 * j))
                    drawRect(RectF(0f, 0f, barW * dsc(0), barH), paint)
                }
            }
        }
    }
}

fun Canvas.drawABSRNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    paint.color = colors[i].toColorInt()
    drawAltBarSeparateRot(scale, w, h, paint)
}

class AltBarSeparateRotView(ctx : Context) : View(ctx) {

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas)
    }

    override fun onTouchEvent(event : MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var dir : Float = 0f, var prevScale : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
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

    data class ABSRNode(var i : Int = 0, val state : State = State()) {

        private var next : ABSRNode? = null
        private var prev : ABSRNode? = null

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < colors.size - 1) {
                next = ABSRNode(i + 1)
                next?.prev = this
            }
        }

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawABSRNode(i, state.scale, paint)
        }

        fun update(cb : (Float) -> Unit) {
            state.update(cb)
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun getNext(dir : Int, cb : () -> Unit) : ABSRNode {
            var curr : ABSRNode? = prev
            if (dir === 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class AltBarSeparateRot(var i : Int) {

        private var curr : ABSRNode = ABSRNode(0)
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

    data class Renderer(var view : AltBarSeparateRotView) {

        private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val absr : AltBarSeparateRot = AltBarSeparateRot(0)
        private val animator : Animator = Animator(view)

        fun render(canvas : Canvas) {
            canvas.drawColor(backColor)
            absr.draw(canvas, paint)
            animator.animate {
                absr.update {
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            absr.startUpdating {
                animator.start()
            }
        }
    }

    companion object {
        fun create(activity: Activity) : AltBarSeparateRotView {
            val view : AltBarSeparateRotView = AltBarSeparateRotView(activity)
            activity.setContentView(view)
            return view
        }
    }
}

