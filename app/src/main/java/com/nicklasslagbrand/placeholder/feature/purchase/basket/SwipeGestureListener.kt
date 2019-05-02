package com.nicklasslagbrand.placeholder.feature.purchase.basket

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View

class SwipeGestureListener(private val onSwipeTouchListener: OnSwipeTouchListener) :
    GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
        return true
    }

    override fun onFling(event1: MotionEvent, event2: MotionEvent, velocityX: Float, velocityY: Float): Boolean {
        var result = false
        val diffY = event2.y - event1.y

        if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffY <= 0) {
                onSwipeTouchListener.onSwipeUp()
            }
            result = true
        }

        return result
    }

    companion object {
        private const val SWIPE_THRESHOLD = 100
        private const val SWIPE_VELOCITY_THRESHOLD = 100
    }

    class OnSwipeTouchListener(ctx: Context, val onSwipeUp: () -> Unit) : View.OnTouchListener {
        private var gestureDetector: GestureDetector = GestureDetector(ctx, SwipeGestureListener(this))

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            return gestureDetector.onTouchEvent(event)
        }
    }
}
