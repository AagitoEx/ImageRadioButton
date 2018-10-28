package com.aagito.imageradiobutton

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ImageViewCompat
import com.google.android.material.card.MaterialCardView
import java.util.*

class RadioImageButton : MaterialCardView, RadioCheckable {

    //immutable
    val TOP = 0
    val RIGHT = 1
    val BOTTOM = 2
    val LEFT = 3

    //views
    private lateinit var textView: TextView
    private lateinit var imageView: AppCompatImageView

    //mutable
    var mChecked = false
    var mOnClickListener: View.OnClickListener? = null
    var mOnTouchListener: View.OnTouchListener? = null

    //variables
    private var text: String = "Title"
    private var drawableIcon: Drawable? = null
    private var textSize: Float = 14f
    private var iconSize: Float = 48f
    private var textColor: Int = Color.GRAY
    private var pressedTextColor: Int = Color.BLACK
    private var iconColor: Int = Color.GRAY
    private var pressedIconColor: Int = Color.BLACK
    private var btnBackgroundColor: Int = Color.WHITE
    private var pressedBtnBackgroundColor: Int = Color.LTGRAY
    private var iconPosition: Int = TOP

    private val mOnCheckedChangeListeners = ArrayList<RadioCheckable.OnCheckedChangeListener>()

    constructor(context: Context) : super(context) {
        initRadioImageButton()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) {
        parseAttributes(attrs)
        initRadioImageButton()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        parseAttributes(attrs)
        initRadioImageButton()
    }

    private fun parseAttributes(attrs: AttributeSet?) {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.RadioImageButton, 0, 0)

        try {

            text = when {
                ta.getString(R.styleable.RadioImageButton_text) != null -> ta.getString(R.styleable.RadioImageButton_text)
                else -> ""
            }
            drawableIcon = ta.getDrawable(R.styleable.RadioImageButton_drawableIcon)

            textSize = ta.getDimension(R.styleable.RadioImageButton_textSize, 14f)
            iconSize = ta.getDimension(R.styleable.RadioImageButton_iconSize, dpToPx(24).toFloat())

            textColor = ta.getColor(R.styleable.RadioImageButton_textColor, Color.GRAY)
            pressedTextColor = ta.getColor(R.styleable.RadioImageButton_selectedTextColor, Color.BLACK)
            iconColor = ta.getColor(R.styleable.RadioImageButton_iconColor, Color.GRAY)
            pressedIconColor = ta.getColor(R.styleable.RadioImageButton_selectedIconColor, Color.BLACK)
            btnBackgroundColor = ta.getColor(R.styleable.RadioImageButton_backgroundColor, Color.WHITE)
            pressedBtnBackgroundColor = ta.getColor(R.styleable.RadioImageButton_selectedBackgroundColor, Color.LTGRAY)

            iconPosition = ta.getInteger(R.styleable.RadioImageButton_iconPosition, TOP)

        } finally {
            ta.recycle()
        }
    }

    private fun initRadioImageButton() {
        inflateView()
        bindView()
        setCustomTouchListener()
    }

    private fun inflateView() {

        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.custom_radio_image_button, this, true)

        textView = findViewById(R.id.textView)
        imageView = findViewById(R.id.imageView)
    }

    private fun bindView() {

        if (text != "") {
            textView.text = text
        } else {
            textView.visibility = View.GONE
        }

        //card view default values
        //strokeWidth = dpToPx(1)
        cardElevation = dpToPx(1).toFloat()
        strokeColor = iconColor
        setCardBackgroundColor(btnBackgroundColor)
        setContentPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8))

        //image view default properties
        ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(iconColor))
        imageView.layoutParams.height = iconSize.toInt()
        imageView.layoutParams.width = iconSize.toInt()
        imageView.setImageDrawable(drawableIcon)

        //text view default properties
        textView.setTextColor(textColor)
        textView.textSize = textSize

        //align icon
        val imageViewParams = imageView.layoutParams as RelativeLayout.LayoutParams
        val textViewParams = textView.layoutParams as RelativeLayout.LayoutParams

        //icon position
        when (iconPosition) {

            TOP -> {
                imageViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                imageViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                textViewParams.addRule(RelativeLayout.BELOW, R.id.imageView)
                textViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            }
            BOTTOM -> {
                textViewParams.addRule(RelativeLayout.ALIGN_PARENT_TOP)
                textViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
                imageViewParams.addRule(RelativeLayout.BELOW, R.id.textView)
                imageViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL)
            }
            LEFT -> {
                imageViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                imageViewParams.addRule(RelativeLayout.CENTER_VERTICAL)
                textViewParams.addRule(RelativeLayout.END_OF, R.id.imageView)
                textViewParams.addRule(RelativeLayout.CENTER_VERTICAL)
            }
            RIGHT -> {
                textViewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                textViewParams.addRule(RelativeLayout.CENTER_VERTICAL)
                imageViewParams.addRule(RelativeLayout.END_OF, R.id.textView)
                imageViewParams.addRule(RelativeLayout.CENTER_VERTICAL)
            }
        }

        imageView.layoutParams = imageViewParams
        textView.layoutParams = textViewParams
    }

    private fun dpToPx(dp: Int): Int {
        val displayMetric = context.resources.displayMetrics
        return dp * (displayMetric.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        mOnClickListener = l
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCustomTouchListener() {
        super.setOnTouchListener(TouchListener())
    }

    private fun onTouchDown(motionEvent: MotionEvent) {
        isChecked = true
    }

    private fun onTouchUp(motionEvent: MotionEvent) {
        // Handle user defined click listeners
        mOnClickListener?.onClick(this)
    }


//================================================================================
// Public methods
//================================================================================

    fun setCheckedState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animateToSelected()
        }
    }

    fun setNormalState() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            animateToNormal()
        }
    }

    fun getText(): String {
        return text
    }

    fun setText(text: String) {
        this.text = text
    }

    fun getDrawableIcon(): Drawable? {
        return drawableIcon
    }

    fun setDrawableIcon(drawableIcon: Drawable) {
        this.drawableIcon = drawableIcon
    }


    //================================================================================
    // Checkable implementation
    //================================================================================

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            if (!mOnCheckedChangeListeners.isEmpty()) {
                for (i in mOnCheckedChangeListeners.indices) {
                    mOnCheckedChangeListeners.get(i).onCheckedChanged(this, mChecked)
                }
            }
            if (mChecked) {
                setCheckedState()
            } else {
                setNormalState()
            }
        }
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun addOnCheckChangeListener(onCheckedChangeListener: RadioCheckable.OnCheckedChangeListener?) {
        onCheckedChangeListener?.let { mOnCheckedChangeListeners.add(it) }
    }

    override fun removeOnCheckChangeListener(onCheckedChangeListener: RadioCheckable.OnCheckedChangeListener?) {
        mOnCheckedChangeListeners.remove(onCheckedChangeListener)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateToSelected() {

        //float animator for elevation
        ValueAnimator.ofFloat(dpToPx(1).toFloat(), dpToPx(8).toFloat()).apply {
            duration = 300
            addUpdateListener { cardElevation = it.animatedValue as Float }
            start()
        }

        ValueAnimator.ofArgb(iconColor, pressedIconColor).apply {
            duration = 300
            addUpdateListener {
                ImageViewCompat.setImageTintList(imageView,
                        ColorStateList.valueOf(it.animatedValue as Int))
                strokeColor = it.animatedValue as Int
            }
            start()
        }

        //color animator for text color
        ValueAnimator.ofArgb(textColor, pressedTextColor).apply {
            duration = 300
            addUpdateListener {
                textView.setTextColor(it.animatedValue as Int)
            }
            start()
        }

        ValueAnimator.ofArgb(btnBackgroundColor, pressedBtnBackgroundColor).apply {
            duration = 300
            addUpdateListener { setCardBackgroundColor(it.animatedValue as Int) }
            start()
        }

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun animateToNormal() {

        //float animator for elevation
        ValueAnimator.ofFloat(dpToPx(8).toFloat(), dpToPx(1).toFloat()).apply {
            duration = 300
            addUpdateListener { cardElevation = it.animatedValue as Float }
            start()
        }

        //color animator for icon color
        ValueAnimator.ofArgb(pressedIconColor, iconColor).apply {
            duration = 300
            addUpdateListener {
                ImageViewCompat.setImageTintList(imageView,
                        ColorStateList.valueOf(it.animatedValue as Int))
                strokeColor = it.animatedValue as Int
            }
            start()
        }

        //color animator for text color
        ValueAnimator.ofArgb(pressedTextColor, textColor).apply {
            duration = 300
            addUpdateListener {
                textView.setTextColor(it.animatedValue as Int)
            }
            start()
        }

        ValueAnimator.ofArgb(pressedBtnBackgroundColor, btnBackgroundColor).apply {
            duration = 300
            addUpdateListener { setCardBackgroundColor(it.animatedValue as Int) }
            start()
        }

    }


    private inner class TouchListener : View.OnTouchListener {

        override fun onTouch(v: View, event: MotionEvent): Boolean {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> onTouchDown(event)
                MotionEvent.ACTION_UP -> onTouchUp(event)
            }
            mOnTouchListener?.onTouch(v, event)
            return true
        }

    }

}