package com.aagito.imageradiobutton

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.IdRes
import androidx.annotation.RequiresApi
import java.util.HashMap

class RadioImageGroup: LinearLayout {

    // Attribute Variables
    private var mCheckedId = View.NO_ID
    private var mProtectFromCheckedChange = false
    // Variables
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
    private val mChildViewsMap = HashMap<Int, View>()
    private var mPassThroughListener: PassThroughHierarchyChangeListener? = null
    private var mChildOnCheckedChangeListener: RadioCheckable.OnCheckedChangeListener? = null


    //================================================================================
    // Constructors
    //================================================================================

    constructor(context: Context): super(context) {
        setupView()
    }

    constructor(context: Context, attrs: AttributeSet): super(context, attrs) {
        parseAttributes(attrs)
        setupView()
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int): super(context, attrs, defStyleAttr) {
        parseAttributes(attrs)
        setupView()
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int): super(context, attrs, defStyleAttr, defStyleRes) {
        parseAttributes(attrs)
        setupView()
    }

    //================================================================================
    // Init & inflate methods
    //================================================================================
    private fun parseAttributes(attrs: AttributeSet) {
        val a = getContext().obtainStyledAttributes(attrs,
                R.styleable.RadioImageGroup, 0, 0)
        try {
            mCheckedId = a.getResourceId(R.styleable.RadioImageGroup_presetRadioCheckedId, View.NO_ID)

        } finally {
            a.recycle()
        }
    }

    // Template method
    private fun setupView() {
        orientation = LinearLayout.HORIZONTAL
        mChildOnCheckedChangeListener = CheckedStateTracker()
        mPassThroughListener = PassThroughHierarchyChangeListener()
        super.setOnHierarchyChangeListener(mPassThroughListener)
    }


    //================================================================================
    // Overriding default behavior
    //================================================================================
    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is RadioCheckable) {
            val button = child as RadioCheckable
            if (button.isChecked) {
                mProtectFromCheckedChange = true
                if (mCheckedId != View.NO_ID) {
                    setCheckedStateForView(mCheckedId, false)
                }
                mProtectFromCheckedChange = false
                setCheckedId(child.id, true)
            }
        }

        super.addView(child, index, params)
    }

    override fun setOnHierarchyChangeListener(listener: ViewGroup.OnHierarchyChangeListener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener!!.mOnHierarchyChangeListener = listener
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        // checks the appropriate radio button as requested in the XML file
        if (mCheckedId != View.NO_ID) {
            mProtectFromCheckedChange = true
            setCheckedStateForView(mCheckedId, true)
            mProtectFromCheckedChange = false
            setCheckedId(mCheckedId, true)
        }
    }

    private fun setCheckedId(@IdRes id: Int, isChecked: Boolean) {
        mCheckedId = id
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener!!.onCheckedChanged(this, mChildViewsMap[id], isChecked, mCheckedId)
        }
    }

    override fun checkLayoutParams(p: ViewGroup.LayoutParams): Boolean {
        return p is LinearLayout.LayoutParams
    }

    fun clearCheck() {
        check(View.NO_ID)
    }

    fun check(@IdRes id: Int) {
        // don't even bother
        if (id != View.NO_ID && id == mCheckedId) {
            return
        }

        if (mCheckedId != View.NO_ID) {
            setCheckedStateForView(mCheckedId, false)
        }

        if (id != View.NO_ID) {
            setCheckedStateForView(id, true)
        }

        setCheckedId(id, true)
    }

    private fun setCheckedStateForView(viewId: Int, checked: Boolean) {
        var checkedView: View?
        checkedView = mChildViewsMap[viewId]
        if (checkedView == null) {
            checkedView = findViewById(viewId)
            if (checkedView != null) {
                mChildViewsMap[viewId] = checkedView
            }
        }
        if (checkedView != null && checkedView is RadioCheckable) {
            (checkedView as RadioCheckable).setChecked(checked)
        }
    }

    override fun generateLayoutParams(attrs: AttributeSet): LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(context, attrs)
    }
    //================================================================================
    // Public methods
    //================================================================================


    fun setOnCheckedChangeListener(onCheckedChangeListener: OnCheckedChangeListener) {
        mOnCheckedChangeListener = onCheckedChangeListener
    }

    fun getOnCheckedChangeListener(): OnCheckedChangeListener? {
        return mOnCheckedChangeListener
    }


    //================================================================================
    // Nested classes
    //================================================================================
    interface OnCheckedChangeListener {
        fun onCheckedChanged(radioGroup: View, radioButton: View?, isChecked: Boolean, checkedId: Int)
    }

    //================================================================================
    // Inner classes
    //================================================================================
    private inner class CheckedStateTracker : RadioCheckable.OnCheckedChangeListener {
        override fun onCheckedChanged(radioGroup: View, isChecked: Boolean) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return
            }

            mProtectFromCheckedChange = true
            if (mCheckedId != View.NO_ID) {
                setCheckedStateForView(mCheckedId, false)
            }
            mProtectFromCheckedChange = false

            val id = radioGroup.id
            setCheckedId(id, true)
        }
    }

    private inner class PassThroughHierarchyChangeListener : ViewGroup.OnHierarchyChangeListener {
        var mOnHierarchyChangeListener: ViewGroup.OnHierarchyChangeListener? = null

        /**
         * {@inheritDoc}
         */
        override fun onChildViewAdded(parent: View, child: View) {
            if (parent === this@RadioImageGroup && child is RadioCheckable) {
                var id = child.id
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = View.generateViewId()
                    child.id = id
                }
                (child as RadioCheckable).addOnCheckChangeListener(
                        mChildOnCheckedChangeListener)
                mChildViewsMap[id] = child
            }

            mOnHierarchyChangeListener?.onChildViewAdded(parent, child)
        }

        /**
         * {@inheritDoc}
         */
        override fun onChildViewRemoved(parent: View, child: View) {
            if (parent === this@RadioImageGroup && child is RadioCheckable) {
                (child as RadioCheckable).removeOnCheckChangeListener(mChildOnCheckedChangeListener)
            }
            mChildViewsMap.remove(child.id)
            mOnHierarchyChangeListener?.onChildViewRemoved(parent, child)
        }
    }

}