package com.tanjiajun.jsonrecyclerview.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.tanjiajun.jsonrecyclerview.R
import com.tanjiajun.jsonrecyclerview.DEFAULT_TEXT_SIZE_SP

/**
 * Created by TanJiaJun on 5/30/21.
 */
class JSONItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private lateinit var tvLeft: TextView
    private lateinit var ivIcon: ImageView
    private lateinit var tvRight: TextView

    /**
     * Set the scaled pixel text size.
     * 设置文本大小。
     */
    var textSize = DEFAULT_TEXT_SIZE_SP
        set(value) {
            // 范围是[12.0F,30.0F]
            field = when {
                value < 12.0F -> 12.0F
                value > 30.0F -> 30.0F
                else -> value
            }
            // 设置左边文本的文字大小
            tvLeft.textSize = field
            // 设置展示展开和收缩图标的大小
            val size = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                field,
                resources.displayMetrics
            ).toInt()
            ivIcon.layoutParams = (ivIcon.layoutParams as LayoutParams).apply {
                width = size
                height = size
            }
            // 设置右边文本的文字大小
            tvRight.textSize = field
        }

    init {
        initView()
    }

    private fun initView() {
        orientation = VERTICAL
        LayoutInflater.from(context).inflate(R.layout.item_json_view, this, true)
        tvLeft = findViewById(R.id.tv_left)
        ivIcon = findViewById(R.id.iv_icon)
        tvRight = findViewById(R.id.tv_right)
    }

    fun setRightColor(@ColorInt textColor: Int) {
        tvRight.setTextColor(textColor)
    }

    fun hideLeft() {
        tvLeft.visibility = GONE
    }

    fun showLeft(text: CharSequence?) {
        tvLeft.visibility = VISIBLE
        text?.let { tvLeft.text = text }
    }

    fun hideIcon() {
        ivIcon.visibility = GONE
    }

    fun showIcon(isExpand: Boolean) {
        ivIcon.visibility = VISIBLE
        ivIcon.setImageResource(if (isExpand) R.drawable.json_viewer_expand else R.drawable.json_viewer_collapse)
        ivIcon.contentDescription =
            resources.getString(if (isExpand) R.string.expand else R.string.close)
    }

    fun hideRight() {
        tvRight.visibility = GONE
    }

    fun showRight(text: CharSequence?) {
        tvRight.visibility = VISIBLE
        text?.let { tvRight.text = text }
    }

    fun getRightText(): CharSequence? =
        tvRight.text

    fun setRightTextClickListener(listener: OnClickListener) =
        tvRight.setOnClickListener(listener)

    fun addViewNoInvalidate(childView: View) {
        childView.layoutParams
            ?.let {
                // 调用addViewInLayout方法，使其在布局（layout）的时候添加View，并且添加到View树的最后一个位置
                addViewInLayout(childView, -1, it)
            }
            ?: run {
                // 如果子View没有LayoutParams，就创建一个默认的LayoutParams，宽度是MATCH_PARENT，高度是WRAP_CONTENT
                generateDefaultLayoutParams()
                    ?.let { addViewInLayout(childView, -1, it) }
                    ?: throw IllegalArgumentException("generateDefaultLayoutParams() cannot return null")
            }
    }

}