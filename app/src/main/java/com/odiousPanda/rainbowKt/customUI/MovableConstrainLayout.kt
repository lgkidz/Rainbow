package com.odiousPanda.rainbowKt.customUI

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.animation.DecelerateInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MovableConstrainLayout : ConstraintLayout, OnTouchListener {
    private var downRawY = 0f
    private var dY = 0f
    private var callback: OnPositionChangedCallback? = null

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    ) {
        init()
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setOnTouchListener(this)
    }

    fun setCallback(callback: OnPositionChangedCallback?) {
        this.callback = callback
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val layoutParams =
            view.layoutParams as LayoutParams
        val viewHeight = view.height
        val viewParent = view.parent as View
        val parentHeight = viewParent.height
        val action = motionEvent.action
        val bottomBoundary = viewHeight.toFloat() * 58 / 100
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                downRawY = motionEvent.rawY
                dY = view.y - downRawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                var newY = motionEvent.rawY + dY
                newY = max(
                    parentHeight.toFloat() / 2,
                    newY
                ) // Don't allow the layout past the top of the parent /2
                newY = min(
                    parentHeight - bottomBoundary - layoutParams.bottomMargin,
                    newY
                ) // Don't allow the layout past the bottom of the parent
                view.animate()
                    .y(newY)
                    .setDuration(0)
                    .start()
                return true
            }
            MotionEvent.ACTION_UP -> {
                val upRawY = motionEvent.rawY
                val upDY = upRawY - downRawY
                var restY = parentHeight.toFloat() / 2
                if (abs(upDY) < bottomBoundary / 2) {
                    if (upRawY > parentHeight.toFloat() / 2 + bottomBoundary / 2) {
                        restY = parentHeight - bottomBoundary - layoutParams.bottomMargin
                    }
                } else {
                    if (upDY > 0) {
                        restY = parentHeight - bottomBoundary - layoutParams.bottomMargin
                    }
                }
                view.animate()
                    .y(restY)
                    .setInterpolator(DecelerateInterpolator())
                    .setDuration(SNAP_DURATION.toLong())
                    .start()
                callback?.onMoved(restY)
            }
        }
        return super.onTouchEvent(motionEvent)
    }

    interface OnPositionChangedCallback {
        fun onMoved(y: Float)
    }

    companion object {
        const val SNAP_DURATION = 150
    }
}