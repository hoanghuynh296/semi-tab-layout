package vn.semicolon.tablayout

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_tab_item.view.*
import vn.semicolon.lib.tablayout.SemiTabItem

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val item = TabItem(this).apply {
            text = "chatterbox"
            iconId = R.drawable.ic_launcher_foreground
        }
        val item2 = TabItem(this).apply {
            text = "garage sale"
            iconId = R.drawable.ic_launcher_foreground
        }
        tab.addTab(item)
        tab.addTab(item2)
    }
}

class TabItem : SemiTabItem {
    override fun onCreateView(): View {
        return LayoutInflater.from(context).inflate(R.layout.layout_tab_item, null)
    }

    override fun onViewCreated(v: View?) {
        super.onViewCreated(v)
        tabItem_image?.setImageResource(iconId)
        tabItem_title?.text = text
    }

    var iconId: Int = -1
    var text: String = ""

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
            super(context, attrs, defStyleAttr) {
    }
}