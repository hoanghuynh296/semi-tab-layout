package vn.semicolon.lib.tablayout

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import vn.semicolon.tablayout.R


class SemiTabLayout : LinearLayout, ISemiTabBehaviour {

    abstract class OnTabSelectedListener {
        /**
         * Called when a tab enters the selected state.
         *
         * @param tab The tab that was selected
         */
        open fun onTabSelected(tab: SemiTabItem) {}

        /**
         * Called when a tab exits the selected state.
         *
         * @param tab The tab that was unselected
         */
        open fun onTabUnselected(tab: SemiTabItem) {}

        /**
         * Called when a tab that is already selected is chosen again by the user. Some applications may
         * use this action to return to the top level of a category.
         *
         * @param tab The tab that was reselected.
         */
        open fun onTabReselected(tab: SemiTabItem) {}
    }

    private var viewPager: ViewPager? = null
    private var selectedIndex: Int = -1
    private var callback: OnTabSelectedListener? = null

    fun setOnTabSelectedListener(callback: OnTabSelectedListener) {
        this.callback = callback
    }

    private var tabs: MutableList<SemiTabItem> = ArrayList()
    override fun addTab(tab: SemiTabItem) {
        tabs.add(tab)
        if (isAttachedToWindow) {
            addView(tab)
        }
    }

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        checkIfThisTabMustBeSelected(child)
    }

    private fun checkIfThisTabMustBeSelected(child: View?) {
        if (child !is SemiTabItem) return
        val index = indexOfChild(child)
        if (index == selectedIndex)
            post {
                doSelectTab(index)
            }
    }

    override fun addView(child: View?) {
        if (child is SemiTabItem)
            validateTabBeforeAdd(child)
        super.addView(child)
    }

    override fun setupWithViewPager(vp: ViewPager) {
        viewPager = vp
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                doSelectTab(position)
            }
        })
    }

    override fun selectNothing() {
        doSelectTab(-1)
    }


    override fun selectTab(position: Int) {
        doSelectTab(position)
    }

    override fun selectNext() {
        doSelectTab(selectedIndex + 1)
    }

    override fun selectPrevious() {
        doSelectTab(selectedIndex - 1)
    }

    private fun setTabIsSelected(tab: SemiTabItem, isSelected: Boolean) {
        tab.isSelected = isSelected
//        if (tab is ViewGroup)
//            for (i in 0 until tab.childCount)
//                setTabIsSelected(tab.getChildAt(i), isSelected)
    }

    private fun doSelectTab(index: Int) {
        if (selectedIndex == index)
            onTabReselected(index)
        else {
            onTabUnselected(selectedIndex)
            selectedIndex = index
            onTabSelected(index)
        }
    }


    private fun onTabUnselected(index: Int) {
        getTabAt(index)?.let {
            callback?.onTabUnselected(it)
        }
    }

    private fun onTabReselected(index: Int) {
        getTabAt(index)?.let {
            callback?.onTabReselected(it)
        }
    }

    private fun onTabUnSelected(index: Int) {
        val v = getTabAt(index)?.let {
            setTabIsSelected(it, false)
            setTabColorFilter(it, false)
        }
    }

    fun getTabAt(index: Int): SemiTabItem? {
        if (index < 0 || index >= tabs.size) return null
        return tabs[index]
    }

    fun setTabEnable(isEnable: Boolean, vararg index: Int) {
        index.forEach {
            getTabAt(it)?.isEnabled = isEnable
        }
    }

    fun getTabs(): List<SemiTabItem> {
        val result = ArrayList<SemiTabItem>()
        for (i in 0 until childCount) {
            getTabAt(i)?.let {
                result.add(it)
            }
        }
        return result
    }

    private fun onTabSelected(index: Int) {
        getTabAt(index)?.let {
            setTabIsSelected(it, true)
            setTabColorFilter(it, true)
            callback?.onTabSelected(it)
        }
        fun doUnSelectOther(selectedIndex: Int) {
            for (i in 0 until childCount) {
                if (selectedIndex != i)
                    onTabUnSelected(i)
            }
        }
        doUnSelectOther(index)
        viewPager?.currentItem = index
    }

    constructor(context: Context) : super(context) {
        init(context, null, -1)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, -1)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr)
    }

    private fun convertXmlViewToTabItem(): List<SemiTabItem> {
        fun copyAttr(v: View, tab: SemiTabItem) {
            tab.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, v.paddingBottom)
            v.setPadding(0, 0, 0, 0) // clear child padding, it'll be SemiTabItem job from now
            tab.layoutParams = v.layoutParams
            val newLp = FrameLayout.LayoutParams(v.layoutParams)
            newLp.width = LayoutParams.WRAP_CONTENT // set child to not care width height
            newLp.height = LayoutParams.WRAP_CONTENT
            newLp.gravity = Gravity.CENTER
            v.layoutParams = newLp
        }

        val result = ArrayList<SemiTabItem>()
        for (i in 0 until childCount) {
            getChildAt(i).let { view ->
                if (view is SemiTabItem) {
                    result.add(view)
                } else {
                    val tabItem = object : SemiTabItem(context) {
                        override fun onCreateView(): View {
                            return view
                        }
                    }
                    copyAttr(view, tabItem)
                    result.add(tabItem)
                }

            }
        }
        return result
    }

    private fun validateTabBeforeAdd(tab: SemiTabItem) {
        setTabColorFilter(tab, false)
        setRipple(tab, rippleColor)
        tab.setOnClickListener {
            doSelectTab(this@SemiTabLayout.indexOfChild(tab))
        }
        if (tab.parent != null)
            (tab.parent as? ViewGroup)?.removeView(tab)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (childCount == 0 && tabs.isEmpty()) return

        if (tabs.isEmpty())
            tabs.addAll(convertXmlViewToTabItem()) // if tabs empty, convert views in xml to tab items

        removeAllViews()

        tabs.forEach { tabItem ->
            addView(tabItem)
        }
    }

    private fun setTabColorFilter(tab: SemiTabItem, isSelected: Boolean) {
        if (selectedColor != Color.TRANSPARENT)
            tab.getViewsToApplyFilter().forEach {
                setColorFilter(if (isSelected) selectedColor else unselectedColor, it)
            }
    }

    private fun setColorFilter(color: Int, view: View) {
        when (view) {
            is ViewGroup -> for (i in 0 until view.childCount)
                setColorFilter(color, view.getChildAt(i))
            is TextView -> view.setTextColor(color)
            is ImageView -> view.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    private val indicatorPaint = Paint()

    private fun drawIndicator(canvas: Canvas?, selectedIndex: Int) {
        if (childCount == 0 || selectedIndex < 0) return
        canvas?.apply {
            val selectedView = getChildAt(selectedIndex)
            val left = selectedView.x
            val top = selectedView.y
            val right = left + selectedView.width
            val bottom = top + selectedView.height
            drawRect(left, top, right, bottom, indicatorPaint)
        }
        invalidate()
    }

    private var indicatorColor: Int = Color.WHITE
    private var rippleColor: Int = Color.TRANSPARENT
    private var selectedColor: Int = Color.TRANSPARENT
    private var unselectedColor: Int = Color.TRANSPARENT
    override fun onDraw(canvas: Canvas?) {
        drawIndicator(canvas, selectedIndex)
        super.onDraw(canvas)

    }

    private fun setRipple(view: View, rippleColor: Int) {
        val currentColor =
            if (view.background is Color) (view.background as ColorDrawable).color else Color.TRANSPARENT
        val newColor = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_pressed),
                intArrayOf(android.R.attr.state_focused),
                intArrayOf(android.R.attr.state_activated),
                intArrayOf()
            ),
            intArrayOf(rippleColor, rippleColor, rippleColor, currentColor)
        )
        view.background = RippleDrawable(newColor, ColorDrawable(currentColor), null)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.SemiTabLayout, defStyleAttr, 0)
            .apply {
                indicatorColor = getColor(R.styleable.SemiTabLayout_indicatorColor, indicatorColor)
                rippleColor = getColor(R.styleable.SemiTabLayout_rippleColor, rippleColor)
                selectedColor = getColor(R.styleable.SemiTabLayout_selectedColor, selectedColor)
                unselectedColor =
                    getColor(R.styleable.SemiTabLayout_unselectedColor, unselectedColor)
                selectedIndex =
                    getInt(R.styleable.SemiTabLayout_defaultSelectedIndex, selectedIndex)
            }
        indicatorPaint.color = indicatorColor
        indicatorPaint.style = Paint.Style.FILL
    }
}