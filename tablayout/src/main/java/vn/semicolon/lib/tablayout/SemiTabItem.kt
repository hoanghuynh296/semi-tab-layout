package vn.semicolon.lib.tablayout

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout

abstract class SemiTabItem : FrameLayout {

    protected abstract fun onCreateView(): View
    fun getTabView() = view

    /**
     * return list to apply color filter when selected / unselected
     */
    open fun getViewsToApplyFilter(): List<View> {
        return listOf(this)
    }

    private var view: View? = null

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

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        isFocusable = true
        isClickable = true

        val lp = (layoutParams as? LinearLayout.LayoutParams) ?: LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        lp.weight = 1.0f
        layoutParams = lp
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        view = onCreateView()
        val lp =
            (view?.layoutParams as? LayoutParams) ?: LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
        lp.gravity = Gravity.CENTER
        view?.layoutParams = lp
        view?.isClickable = false
        view?.isFocusable = false
        if (view?.parent != null)
            (view?.parent as? ViewGroup)?.removeView(view)
        addView(view)
        onViewCreated(view)
    }

    open protected fun onViewCreated(v: View?) {

    }
}
