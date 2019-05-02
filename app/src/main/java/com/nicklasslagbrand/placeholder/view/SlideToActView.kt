package com.nicklasslagbrand.placeholder.view

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.util.Xml
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.nicklasslagbrand.placeholder.R
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

/**
 *  Class representing the custom view, SlideToActView.
 *
 *  SlideToActView is an elegant material designed slider, that enrich your app
 *  with a "Slide-to-unlock" like widget.
 *
 *  Original class was downloaded from https://github.com/cortinico/slidetoact/
 *  Downloaded version: 0.5.1
 *
 *  There were some aspects modified in original implementation:
 *  - Hardcoded required typeface for the text
 *  - Implemented border for the outer area
 *  - Disabled complete icon after the animation
 */
@SuppressWarnings("MagicNumber", "LargeClass", "ComplexMethod", "ComplexCondition")
class SlideToActView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.slideToActViewStyle
) : View(context, attrs, defStyleAttr) {

    /* -------------------- LAYOUT BOUNDS -------------------- */

    private var desiredSliderHeightDp: Float = 72F
    private var desiredSliderWidthDp: Float = 280F
    private var desiredSliderHeight: Int = 0
    private var desiredSliderWidth: Int = 0

    /* -------------------- MEMBERS -------------------- */

    /** Height of the drawing area */
    private var areaHeight: Int = 0
    /** Width of the drawing area */
    private var areaWidth: Int = 0
    /** Actual Width of the drawing area, used for animations */
    private var actualAreaWidth: Int = 0
    /** Border Radius, default to mAreaHeight/2, -1 when not initialized */
    private var borderRadius: Int = -1
    /** Border width, default -1 when not initialized */
    private var borderWidth: Int = -1
    /** Margin of the cursor from the outer area */
    private var actualAreaMargin: Int
    private val originAreaMargin: Int

    /** Text message */
    var text: CharSequence = ""
        set(value) {
            field = value
            invalidate()
        }

    /** Typeface for the text field */
    var typeFace = Typeface.DEFAULT
        set(value) {
            field = value
            textPaint.typeface = value
            invalidate()
        }

    /** Size for the text message */
    private val textSize: Int

    private var textYPosition = -1f
    private var textXPosition = -1f

    /** Outer color used by the slider (primary) */
    var outerColor: Int = 0
        set(value) {
            field = value
            outerPaint.color = value
            drawableArrow.setTint(value)
            invalidate()
        }

    /** Inner color used by the slider (secondary, icon and border) */
    var innerColor: Int = 0
        set(value) {
            field = value
            innerPaint.color = value
            invalidate()
        }

    var textColor: Int = 0
        set(value) {
            field = value
            textPaint.color = value
            invalidate()
        }

    /** Slider cursor position (between 0 and (`reaWidth - mAreaHeight)) */
    private var position: Int = 0
        set(value) {
            field = value
            if (areaWidth - areaHeight == 0) {
                // Avoid 0 division
                positionPerc = 0f
                positionPercInv = 1f
                return
            }
            positionPerc = value.toFloat() / (areaWidth - areaHeight).toFloat()
            positionPercInv = 1 - value.toFloat() / (areaWidth - areaHeight).toFloat()
        }
    /** Slider cursor position in percentage (between 0f and 1f) */
    private var positionPerc: Float = 0f
    /** 1/positionPerc */
    private var positionPercInv: Float = 1f

    /* -------------------- ICONS -------------------- */

    private val iconMargin: Int
    /** Margin for Arrow Icon */
    private var arrowMargin: Int
    /** Current angle for Arrow Icon */
    private var arrowAngle: Float = 0f
    /** Margin for Tick Icon */
    private var tickMargin: Int

    /** Arrow drawable */
    private val drawableArrow: VectorDrawableCompat

    /** Tick drawable, is actually an AnimatedVectorDrawable */
//    private val mDrawableTick: Drawable
    private var flagDrawTick: Boolean = false

    /** The icon for the drawable */
    private var icon: Int = R.drawable.ic_swipe_to_activate

    /* -------------------- PAINT & DRAW -------------------- */
    /** Paint used for outer elements */
    private val outerPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** Paint used for inner elements */
    private val innerPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** Paint used for text elements */
    private val textPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /** Inner rectangle (used for arrow rotation) */
    private var innerRect: RectF
    /** Outer rectangle (used for area drawing) */
    private var outerRect: RectF
    /** Outer border rectangle (used for drawing area border) */
    private var outerBorderRect: RectF
    /** Grace value, when positionPerc > mGraceValue slider will perform the 'complete' operations */
    private val graceValue: Float = 0.8F

    /** Last X coordinate for the touch event */
    private var lastX: Float = 0F
    /** Flag to understand if user is moving the slider cursor */
    private var flagMoving: Boolean = false

    /** Private flag to check if the slide gesture have been completed */
    private var isCompleted = false

    private var drawOutAreaBorder = false

    /** Public flag to lock the slider */
    var isLocked = false

    /** Public flag to lock the rotation icon */
    var isRotateIcon = true

    /** Public flag to enable complete animation */
    var isAnimateCompletion = true

    /** Public Slide event listeners */
    var onSlideToActAnimationEventListener: OnSlideToActAnimationEventListener? = null
    var onSlideCompleteListener: OnSlideCompleteListener? = null
    var onSlideResetListener: OnSlideResetListener? = null
    var onSlideUserFailedListener: OnSlideUserFailedListener? = null

    init {
        val actualOuterColor: Int
        val actualInnerColor: Int
        val actualTextColor: Int

        val layoutAttrs: TypedArray = context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.SlideToActView, defStyleAttr, R.style.AppTheme
        )
        try {
            desiredSliderHeight =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, desiredSliderHeightDp, resources.displayMetrics)
                    .toInt()
            desiredSliderWidth =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, desiredSliderWidthDp, resources.displayMetrics)
                    .toInt()
            desiredSliderHeight =
                layoutAttrs.getDimensionPixelSize(R.styleable.SlideToActView_slider_height, desiredSliderHeight)

            borderRadius = layoutAttrs.getDimensionPixelSize(R.styleable.SlideToActView_border_radius, -1)
            borderWidth = layoutAttrs.getDimensionPixelSize(R.styleable.SlideToActView_outer_border_width, -1)

            val defaultOuter = ContextCompat.getColor(this.context, R.color.colorPrimary)
            val defaultWhite = ContextCompat.getColor(this.context, R.color.md_white_1000)

            actualOuterColor = layoutAttrs.getColor(R.styleable.SlideToActView_outer_color, defaultOuter)
            actualInnerColor = layoutAttrs.getColor(R.styleable.SlideToActView_inner_color, defaultWhite)
            actualTextColor = layoutAttrs.getColor(R.styleable.SlideToActView_text_color, defaultWhite)

            text = layoutAttrs.getString(R.styleable.SlideToActView_text) as CharSequence
//            typeFace = layoutAttrs.getInt(R.styleable.SlideToActView_text_style, 0)
            typeFace = ResourcesCompat.getFont(context, R.font.muli_bold)

            isLocked = layoutAttrs.getBoolean(R.styleable.SlideToActView_slider_locked, false)
            isRotateIcon = layoutAttrs.getBoolean(R.styleable.SlideToActView_rotate_icon, true)
            isAnimateCompletion = layoutAttrs.getBoolean(R.styleable.SlideToActView_animate_completion, true)

            textSize = layoutAttrs.getDimensionPixelSize(
                R.styleable.SlideToActView_text_size,
                resources.getDimensionPixelSize(R.dimen.default_text_size)
            )
            originAreaMargin = layoutAttrs.getDimensionPixelSize(
                R.styleable.SlideToActView_area_margin,
                resources.getDimensionPixelSize(R.dimen.default_area_margin)
            )
            actualAreaMargin = originAreaMargin

            icon = layoutAttrs.getResourceId(R.styleable.SlideToActView_slider_icon, R.drawable.ic_swipe_to_activate)
        } finally {
            layoutAttrs.recycle()
        }

        drawOutAreaBorder = borderWidth != -1

        innerRect = RectF(
            (actualAreaMargin + position).toFloat(), actualAreaMargin.toFloat(),
            (areaHeight + position).toFloat() - actualAreaMargin.toFloat(),
            areaHeight.toFloat() - actualAreaMargin.toFloat()
        )

        var outerRectLeft = actualAreaWidth.toFloat()
        var outerRectTop = 0f
        var outerRectRight = areaWidth.toFloat() - actualAreaWidth.toFloat()
        var outerRectBottom = areaHeight.toFloat() - borderWidth

        if (drawOutAreaBorder) {
            outerRectLeft += borderWidth
            outerRectTop += borderWidth
            outerRectRight -= borderWidth
            outerRectBottom -= borderWidth
        }

        outerRect = RectF(
            outerRectLeft,
            outerRectTop,
            outerRectRight,
            outerRectBottom
        )

        outerBorderRect = RectF(
            actualAreaWidth.toFloat(),
            0f,
            areaWidth.toFloat() - actualAreaWidth.toFloat(),
            areaHeight.toFloat()
        )

        drawableArrow = parseVectorDrawableCompat(context.resources, icon, context.theme)

//        // Due to bug in the AVD implementation in the support library, we use it only for API < 21
//        mDrawableTick = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            context.resources.getDrawable(R.drawable.animated_ic_check, context.theme) as AnimatedVectorDrawable
//        } else {
//            AnimatedVectorDrawableCompat.create(context, R.drawable.animated_ic_check)!!
//        }

        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = textSize.toFloat()

        outerColor = actualOuterColor
        innerColor = actualInnerColor
        textColor = actualTextColor

        iconMargin = context.resources.getDimensionPixelSize(R.dimen.default_icon_margin)
        arrowMargin = iconMargin
        tickMargin = iconMargin

        // This outline provider force removal of shadow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            outlineProvider = SlideToActOutlineProvider()
        }
    }

    private fun parseVectorDrawableCompat(res: Resources, resId: Int, theme: Resources.Theme): VectorDrawableCompat {
        val parser = res.getXml(resId)
        val attrs = Xml.asAttributeSet(parser)
        var type: Int = parser.next()
        while (type != XmlPullParser.START_TAG && type != XmlPullParser.END_DOCUMENT) {
            type = parser.next()
        }
        if (type != XmlPullParser.START_TAG) {
            throw XmlPullParserException("No start tag found")
        }
        return VectorDrawableCompat.createFromXmlInner(res, parser, attrs, theme)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> Math.min(desiredSliderWidth, widthSize)
            MeasureSpec.UNSPECIFIED -> desiredSliderWidth
            else -> desiredSliderWidth
        }

        val height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> Math.min(desiredSliderHeight, heightSize)
            MeasureSpec.UNSPECIFIED -> desiredSliderHeight
            else -> desiredSliderHeight
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        areaWidth = w
        areaHeight = h
        if (borderRadius == -1) // Round if not set up
            borderRadius = h / 2

        // Text horizontal/vertical positioning (both centered)
        textXPosition = areaWidth.toFloat() / 2
        textYPosition = (areaHeight.toFloat() / 2) - (textPaint.descent() + textPaint.ascent()) / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (canvas == null) return

        if (drawOutAreaBorder) {
            outerRect.set(
                actualAreaWidth.toFloat() + borderWidth,
                0f + borderWidth,
                areaWidth.toFloat() - actualAreaWidth.toFloat() - borderWidth,
                areaHeight.toFloat() - borderWidth
            )
            outerBorderRect.set(
                actualAreaWidth.toFloat(),
                0f,
                areaWidth.toFloat() - actualAreaWidth.toFloat(),
                areaHeight.toFloat()
            )
        } else {
            outerRect.set(
                actualAreaWidth.toFloat(),
                0f,
                areaWidth.toFloat() - actualAreaWidth.toFloat(),
                areaHeight.toFloat()
            )
        }

        if (drawOutAreaBorder) {
            canvas.drawRoundRect(outerBorderRect, borderRadius.toFloat(), borderRadius.toFloat(), innerPaint)
        }

        canvas.drawRoundRect(outerRect, borderRadius.toFloat(), borderRadius.toFloat(), outerPaint)

        // Inner Cursor
        // ratio is used to compute the proper border radius for the inner rect (see #8).
        val ratio = (areaHeight - 2 * actualAreaMargin).toFloat() / areaHeight.toFloat()
        innerRect.set(
            (actualAreaMargin + position).toFloat(),
            actualAreaMargin.toFloat(),
            (areaHeight + position).toFloat() - actualAreaMargin.toFloat(),
            areaHeight.toFloat() - actualAreaMargin.toFloat()
        )
        canvas.drawRoundRect(innerRect, borderRadius.toFloat() * ratio, borderRadius.toFloat() * ratio, innerPaint)

        // Text alpha
        textPaint.alpha = (255 * positionPercInv).toInt()

        // Vertical + Horizontal centering
        canvas.drawText(text.toString(), textXPosition, textYPosition, textPaint)

        // Arrow angle
        if (isRotateIcon) {
            arrowAngle = -180 * positionPerc
            canvas.rotate(arrowAngle, innerRect.centerX(), innerRect.centerY())
        }
        drawableArrow.setBounds(
            innerRect.left.toInt() + arrowMargin,
            innerRect.top.toInt() + arrowMargin,
            innerRect.right.toInt() - arrowMargin,
            innerRect.bottom.toInt() - arrowMargin
        )
        if (drawableArrow.bounds.left <= drawableArrow.bounds.right &&
            drawableArrow.bounds.top <= drawableArrow.bounds.bottom
        ) {
            drawableArrow.draw(canvas)
        }

        if (isRotateIcon) {
            canvas.rotate(-1 * arrowAngle, innerRect.centerX(), innerRect.centerY())
        }

//        // Tick drawing
//        mDrawableTick.setBounds(
//            actualAreaWidth + mTickMargin,
//            mTickMargin,
//            mAreaWidth - mTickMargin - actualAreaWidth,
//            mAreaHeight - mTickMargin)
//
//        // Tinting the tick with the proper implementation method
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mDrawableTick.setTint(innerColor)
//        } else {
//            (mDrawableTick as AnimatedVectorDrawableCompat).setTint(innerColor)
//        }
//
//        if (mFlagDrawTick) {
//            mDrawableTick.draw(canvas)
//        }
    }

    // Intentionally override `performClick` to do not lose accessibility support.
    @Suppress("RedundantOverride")
    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null && isEnabled) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (checkInsideButton(event.x, event.y)) {
                        flagMoving = true
                        lastX = event.x
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        // Clicking outside the area -> User failed, notify the listener.
                        onSlideUserFailedListener?.onSlideFailed(this, true)
                    }
                    performClick()
                }
                MotionEvent.ACTION_UP -> {
                    parent.requestDisallowInterceptTouchEvent(false)
                    if ((position > 0 && isLocked) || (position > 0 && positionPerc < graceValue)) {
                        // Check for grace value
                        val positionAnimator = ValueAnimator.ofInt(position, 0)
                        positionAnimator.duration = 300
                        positionAnimator.addUpdateListener {
                            position = it.animatedValue as Int
                            invalidateArea()
                        }
                        positionAnimator.start()
                    } else if (position > 0 && positionPerc >= graceValue) {
                        isEnabled = false // Fully disable touch events
                        startAnimationComplete()
                    } else if (flagMoving && position == 0) {
                        // mFlagMoving == true means user successfully grabbed the slider,
                        // but mPosition == 0 means that the slider is released at the beginning
                        // so either a Tap or the user slided back.
                        onSlideUserFailedListener?.onSlideFailed(this, false)
                    }
                    flagMoving = false
                }
                MotionEvent.ACTION_MOVE -> {
                    if (flagMoving) {
                        val diffX = event.x - lastX
                        lastX = event.x
                        increasePosition(diffX.toInt())
                        invalidateArea()
                    }
                }
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun invalidateArea() {
        invalidate(outerRect.left.toInt(), outerRect.top.toInt(), outerRect.right.toInt(), outerRect.bottom.toInt())
    }

    /**
     * Private method to check if user has touched the slider cursor
     * @param x The x coordinate of the touch event
     * @param y The y coordinate of the touch event
     * @return A boolean that informs if user has pressed or not
     */
    private fun checkInsideButton(x: Float, y: Float): Boolean {
        return (0 < y && y < areaHeight && position < x && x < (areaHeight + position))
    }

    /**
     * Private method for increasing/decreasing the position
     * Ensure that position never exits from its range [0, (mAreaWidth - mAreaHeight)]
     *
     * @param inc Increment to be performed (negative if it's a decrement)
     */
    private fun increasePosition(inc: Int) {
        position += inc
        if (position < 0)
            position = 0
        if (position > (areaWidth - areaHeight))
            position = areaWidth - areaHeight
    }

    /**
     * Private method that is performed when user completes the slide
     */
    private fun startAnimationComplete() {
        val animSet = AnimatorSet()

        // Animator that moves the cursor
        val finalPositionAnimator = ValueAnimator.ofInt(position, areaWidth - areaHeight)
        finalPositionAnimator.addUpdateListener {
            position = it.animatedValue as Int
            invalidateArea()
        }

        // Animator that bounce away the cursors
        val marginAnimator =
            ValueAnimator.ofInt(actualAreaMargin, (innerRect.width() / 2).toInt() + actualAreaMargin)
        marginAnimator.addUpdateListener {
            actualAreaMargin = it.animatedValue as Int
            invalidateArea()
        }
        marginAnimator.interpolator = AnticipateOvershootInterpolator(2f)

        // Animator that reduces the outer area (to right)
        val areaAnimator = ValueAnimator.ofInt(0, (areaWidth - areaHeight) / 2)
        areaAnimator.addUpdateListener {
            actualAreaWidth = it.animatedValue as Int
            if (Build.VERSION.SDK_INT >= 21) {
                invalidateOutline()
            }
            invalidateArea()
        }

//        val tickAnimator: ValueAnimator
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
//            // Fallback not using AVD.
//            tickAnimator = ValueAnimator.ofInt(0, 255)
//            tickAnimator.addUpdateListener {
//                mTickMargin = mIconMargin
//                mFlagDrawTick = true
//                mDrawableTick.alpha = it.animatedValue as Int
//                invalidateArea()
//            }
//        } else {
//            // Used AVD Animation.
//            tickAnimator = ValueAnimator.ofInt(0)
//            tickAnimator.addUpdateListener {
//                if (!mFlagDrawTick) {
//                    mTickMargin = mIconMargin
//                    mFlagDrawTick = true
//                    startTickAnimation()
//                    invalidateArea()
//                }
//            }
//        }

        val animators = mutableListOf<Animator>()
        if (position < areaWidth - areaHeight) {
            animators.add(finalPositionAnimator)
        }

        if (isAnimateCompletion) {
            animators.add(marginAnimator)
            animators.add(areaAnimator)
//            animators.add(tickAnimator)
        }

        animSet.playSequentially(animators)

        animSet.duration = 300

        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                onSlideToActAnimationEventListener?.onSlideCompleteAnimationStarted(this@SlideToActView, positionPerc)
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                isCompleted = true
                onSlideToActAnimationEventListener?.onSlideCompleteAnimationEnded(this@SlideToActView)
                onSlideCompleteListener?.onSlideComplete(this@SlideToActView)
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
        animSet.start()
    }

    /**
     * Method that completes the slider
     */
    fun completeSlider() {
        if (!isCompleted) {
            startAnimationComplete()
        }
    }

    /**
     * Method that reset the slider
     */
    fun resetSlider() {
        if (isCompleted) {
            startAnimationReset()
        }
    }

    /**
     * Method that returns the 'mIsCompleted' flag
     * @return True if slider is in the Complete state
     */
    fun isCompleted(): Boolean {
        return this.isCompleted
    }

    /**
     * Private method that is performed when you want to reset the cursor
     */
    private fun startAnimationReset() {
        isCompleted = false
        val animSet = AnimatorSet()

        // Animator that enlarges the outer area
        val tickAnimator = ValueAnimator.ofInt(tickMargin, areaWidth / 2)
        tickAnimator.addUpdateListener {
            tickMargin = it.animatedValue as Int
            invalidateArea()
        }

        // Animator that enlarges the outer area
        val areaAnimator = ValueAnimator.ofInt(actualAreaWidth, 0)
        areaAnimator.addUpdateListener {
            // Now we can hide the tick till the next complete
            flagDrawTick = false
            actualAreaWidth = it.animatedValue as Int
            if (Build.VERSION.SDK_INT >= 21) {
                invalidateOutline()
            }
            invalidateArea()
        }

        val positionAnimator = ValueAnimator.ofInt(position, 0)
        positionAnimator.addUpdateListener {
            position = it.animatedValue as Int
            invalidateArea()
        }

        // Animator that re-draw the cursors
        val marginAnimator = ValueAnimator.ofInt(actualAreaMargin, originAreaMargin)
        marginAnimator.addUpdateListener {
            actualAreaMargin = it.animatedValue as Int
            invalidateArea()
        }
        marginAnimator.interpolator = AnticipateOvershootInterpolator(2f)

        // Animator that makes the arrow appear
        val arrowAnimator = ValueAnimator.ofInt(arrowMargin, iconMargin)
        arrowAnimator.addUpdateListener {
            arrowMargin = it.animatedValue as Int
            invalidateArea()
        }

        marginAnimator.interpolator = OvershootInterpolator(2f)

        if (isAnimateCompletion) {
            animSet.playSequentially(tickAnimator, areaAnimator, positionAnimator, marginAnimator, arrowAnimator)
        } else {
            animSet.playSequentially(positionAnimator)
        }

        animSet.duration = 300

        animSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {
                onSlideToActAnimationEventListener?.onSlideResetAnimationStarted(this@SlideToActView)
            }

            override fun onAnimationCancel(p0: Animator?) {
            }

            override fun onAnimationEnd(p0: Animator?) {
                isEnabled = true
//                stopTickAnimation()
                onSlideToActAnimationEventListener?.onSlideResetAnimationEnded(this@SlideToActView)
                onSlideResetListener?.onSlideReset(this@SlideToActView)
            }

            override fun onAnimationRepeat(p0: Animator?) {
            }
        })
        animSet.start()
    }

    /**
     * Private method to start the Tick AVD animation, with the proper library based on API level.
     */
//    private fun startTickAnimation() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            (mDrawableTick as AnimatedVectorDrawable).start()
//        } else {
//            (mDrawableTick as AnimatedVectorDrawableCompat).start()
//        }
//    }
//
//    /**
//     * Private method to stop the Tick AVD animation, with the proper library based on API level.
//     */
//    private fun stopTickAnimation() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            (mDrawableTick as AnimatedVectorDrawable).stop()
//        } else {
//            (mDrawableTick as AnimatedVectorDrawableCompat).stop()
//        }
//    }

    /**
     * Event handler for the SlideToActView animation events.
     * This event handler can be used to react to animation events from the Slide,
     * the event will be fired whenever an animation start/end.
     */
    interface OnSlideToActAnimationEventListener {

        /**
         * Called when the slide complete animation start. You can perform actions during the complete
         * animations.
         *
         * @param view The SlideToActView who created the event
         * @param threshold The mPosition (in percentage [0f,1f]) where the user has left the cursor
         */
        fun onSlideCompleteAnimationStarted(view: SlideToActView, threshold: Float)

        /**
         * Called when the slide complete animation finish. At this point the slider is stuck in the
         * center of the slider.
         *
         * @param view The SlideToActView who created the event
         */
        fun onSlideCompleteAnimationEnded(view: SlideToActView)

        /**
         * Called when the slide reset animation start. You can perform actions during the reset
         * animations.
         *
         * @param view The SlideToActView who created the event
         */
        fun onSlideResetAnimationStarted(view: SlideToActView)

        /**
         * Called when the slide reset animation finish. At this point the slider will be in the
         * ready on the left of the screen and user can interact with it.
         *
         * @param view The SlideToActView who created the event
         */
        fun onSlideResetAnimationEnded(view: SlideToActView)
    }

    /**
     * Event handler for the slide complete event.
     * Use this handler to react to slide event
     */
    interface OnSlideCompleteListener {
        /**
         * Called when user performed the slide
         * @param view The SlideToActView who created the event
         */
        fun onSlideComplete(view: SlideToActView)
    }

    /**
     * Event handler for the slide react event.
     * Use this handler to inform the user that he can slide again.
     */
    interface OnSlideResetListener {
        /**
         * Called when slides is again available
         * @param view The SlideToActView who created the event
         */
        fun onSlideReset(view: SlideToActView)
    }

    /**
     * Event handler for the user failure with the Widget.
     * You can subscribe to this event to get notified when the user is wrongly
     * interacting with the widget to eventually educate it:
     *
     * - The user clicked outside of the cursor
     * - The user slided but left when the cursor was back to zero
     *
     * You can use this listener to show a Toast or other messages.
     */
    interface OnSlideUserFailedListener {
        /**
         * Called when user failed to interact with the slider slide
         * @param view The SlideToActView who created the event
         * @param isOutside True if user pressed outside the cursor
         */
        fun onSlideFailed(view: SlideToActView, isOutside: Boolean)
    }

    /**
     * Outline provider for the SlideToActView.
     * This outline will suppress the shadow (till the moment when Android will support
     * updatable Outlines).
     */
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private inner class SlideToActOutlineProvider : ViewOutlineProvider() {

        override fun getOutline(view: View?, outline: Outline?) {
            if (view == null || outline == null)
                return

            outline.setRoundRect(
                actualAreaWidth,
                0,
                areaWidth - actualAreaWidth,
                areaHeight,
                borderRadius.toFloat()
            )
        }
    }
}
