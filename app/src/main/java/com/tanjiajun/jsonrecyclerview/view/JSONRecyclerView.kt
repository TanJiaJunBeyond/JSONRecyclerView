package com.tanjiajun.jsonrecyclerview.view

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tanjiajun.jsonrecyclerview.DEFAULT_TEXT_SIZE_SP
import com.tanjiajun.jsonrecyclerview.R
import com.tanjiajun.jsonrecyclerview.adapter.JSONViewAdapter
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by TanJiaJun on 3/24/21.
 */
class JSONRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr) {

    private val adapter = JSONViewAdapter(context)

    init {
        layoutManager = LinearLayoutManager(context)
        setAdapter(adapter)
    }

    /**
     * 绑定JSON字符串数据。
     * Bind the json string data.
     *
     * @param jsonString The json string to bind.（要绑定的JSON字符串。）
     */
    fun bindData(jsonString: String) =
        adapter.bindData(jsonString)

    /**
     * 绑定JSONObject数据。
     * Bind the json object data.
     *
     * @param jsonObject The json object to bind.（要绑定的JSONObject。）
     */
    fun bindData(jsonObject: JSONObject) =
        adapter.bindData(jsonObject)

    /**
     * 绑定JSONArray数据。
     * Bind the json array data.
     *
     * @param jsonArray The json array to bind.（要绑定的JSONArray。）
     */
    fun bindData(jsonArray: JSONArray) =
        adapter.bindData(jsonArray)

    /**
     * 设置JsonItemView的样式。
     * Set the json item view styles.
     *
     * @param textSize The size of all text.（所有文本的大小。）
     * @param textColor The normal text color.（普通文本的颜色）
     * @param keyColor The color of the text of type key.（key类型文本的颜色。）
     * @param stringColor The color of the text of type String.（字符串类型文本的颜色。）
     * @param numberColor The color of the text of type Number.（Number类型文本的颜色。）
     * @param booleanColor The color of text of type Boolean.（Boolean类型文本的颜色。）
     * @param urlColor The color of url text.（url文本的颜色。）
     * @param nullColor The color of null text.（null文本的颜色。）
     */
    @JvmOverloads
    fun setStyles(
        textSize: Float = DEFAULT_TEXT_SIZE_SP,
        @ColorInt textColor: Int = ContextCompat.getColor(context, R.color.default_text_color),
        @ColorInt keyColor: Int = ContextCompat.getColor(context, R.color.default_key_color),
        @ColorInt stringColor: Int = ContextCompat.getColor(context, R.color.default_string_color),
        @ColorInt numberColor: Int = ContextCompat.getColor(context, R.color.default_number_color),
        @ColorInt booleanColor: Int = ContextCompat.getColor(
            context,
            R.color.default_boolean_color
        ),
        @ColorInt urlColor: Int = ContextCompat.getColor(context, R.color.default_url_color),
        @ColorInt nullColor: Int = ContextCompat.getColor(context, R.color.default_null_color)
    ) {
        with(adapter) {
            this.textSize = when {
                textSize < MIN_TEXT_SIZE -> MIN_TEXT_SIZE
                textSize > MAX_TEXT_SIZE -> MAX_TEXT_SIZE
                else -> textSize
            }
            this.textColor = textColor
            this.keyColor = keyColor
            this.stringColor = stringColor
            this.numberColor = numberColor
            this.booleanColor = booleanColor
            this.urlColor = urlColor
            this.nullColor = nullColor
            // 刷新列表
            notifyDataSetChanged()
        }
    }

    private companion object {
        const val MIN_TEXT_SIZE = 12.0F
        const val MAX_TEXT_SIZE = 24.0F
    }

}