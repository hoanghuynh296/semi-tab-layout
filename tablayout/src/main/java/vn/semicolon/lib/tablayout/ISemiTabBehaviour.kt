
package vn.semicolon.lib.tablayout

import androidx.viewpager.widget.ViewPager

interface ISemiTabBehaviour {
    fun selectNext()
    fun selectTab(position: Int)
    fun selectPrevious()
    fun selectNothing()
    fun setupWithViewPager(vp: ViewPager)
    fun addTab(tab: SemiTabItem)
}