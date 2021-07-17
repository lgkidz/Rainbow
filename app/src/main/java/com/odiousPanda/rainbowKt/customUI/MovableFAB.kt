package com.odiousPanda.rainbowKt.customUI

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.OvershootInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class MovableFAB : FloatingActionButton, OnTouchListener {
    private var downRawX = 0f
    private var downRawY = 0f
    private var dX = 0f
    private var dY = 0f

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        setOnTouchListener(this)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        val layoutParams = view.layoutParams as MarginLayoutParams
        val viewWidth = view.width
        val viewHeight = view.height
        val viewParent = view.parent as View
        val parentWidth = viewParent.width
        val parentHeight = viewParent.height
        val action = motionEvent.action
        return if (action == MotionEvent.ACTION_DOWN) {
            downRawX = motionEvent.rawX
            downRawY = motionEvent.rawY
            dX = view.x - downRawX
            dY = view.y - downRawY
            true // Consumed
        } else if (action == MotionEvent.ACTION_MOVE) {
            var newX = motionEvent.rawX + dX
            // Don't allow the FAB past the left hand side of the parent
            newX = max(layoutParams.leftMargin.toFloat(), newX)
            // Don't allow the FAB past the right hand side of the parent
            newX = min(parentWidth - viewWidth - layoutParams.rightMargin.toFloat(), newX)

            var newY = motionEvent.rawY + dY
            // Don't allow the FAB past the top of the parent
            newY = max(layoutParams.topMargin.toFloat(), newY)
            // Don't allow the FAB past the bottom of the parent
            newY = min(parentHeight - viewHeight - layoutParams.bottomMargin.toFloat(), newY)

            view.animate()
                .x(newX)
                .y(newY)
                .setDuration(0)
                .start()

            true // Consumed
        } else if (action == MotionEvent.ACTION_UP) {
            val upRawX = motionEvent.rawX
            val upRawY = motionEvent.rawY
            val upDX = upRawX - downRawX
            val upDY = upRawY - downRawY
            var restX = parentWidth - viewWidth - layoutParams.rightMargin.toFloat()

            if (upRawX < parentWidth / 2 - viewWidth / 2) {
                restX = layoutParams.leftMargin.toFloat()
            }

            view.animate()
                .x(restX)
                .setInterpolator(OvershootInterpolator())
                .setDuration(300)
                .start()

            if (abs(upDX) < CLICK_DRAG_TOLERANCE && abs(upDY) < CLICK_DRAG_TOLERANCE) { // A click
                performClick()
            } else { // A drag
                true // Consumed
            }

        } else {
            super.onTouchEvent(motionEvent)
        }
    }

    companion object {
        // Often, there will be a slight, unintentional, drag when the user taps the FAB, so we need to account for this.
        private const val CLICK_DRAG_TOLERANCE = 10f
    }
}