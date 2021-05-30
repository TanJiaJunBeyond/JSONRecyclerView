package com.tanjiajun.jsonrecyclerview.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tanjiajun.jsonrecyclerview.DEFAULT_TEXT_SIZE_SP
import com.tanjiajun.jsonrecyclerview.R
import com.tanjiajun.jsonrecyclerview.utils.getHierarchyStr
import com.tanjiajun.jsonrecyclerview.utils.isUrl
import com.tanjiajun.jsonrecyclerview.utils.otherwise
import com.tanjiajun.jsonrecyclerview.utils.yes
import com.tanjiajun.jsonrecyclerview.view.JSONItemView
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener

/**
 * Created by TanJiaJun on 5/30/21.
 */
class JSONViewAdapter(private val context: Context) :
    RecyclerView.Adapter<JSONViewAdapter.JsonItemViewHolder>() {

    private var jsonObject: JSONObject? = null
    private var jsonArray: JSONArray? = null

    @ColorInt
    var textColor: Int = ContextCompat.getColor(context, R.color.default_text_color)

    @ColorInt
    var keyColor: Int = ContextCompat.getColor(context, R.color.default_key_color)

    @ColorInt
    var stringColor: Int = ContextCompat.getColor(context, R.color.default_string_color)

    @ColorInt
    var numberColor: Int = ContextCompat.getColor(context, R.color.default_number_color)

    @ColorInt
    var booleanColor: Int = ContextCompat.getColor(context, R.color.default_boolean_color)

    @ColorInt
    var urlColor: Int = ContextCompat.getColor(context, R.color.default_url_color)

    @ColorInt
    var nullColor: Int = ContextCompat.getColor(context, R.color.default_null_color)

    var textSize: Float = DEFAULT_TEXT_SIZE_SP

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JsonItemViewHolder =
        JsonItemViewHolder(JSONItemView(context))

    override fun onBindViewHolder(holder: JsonItemViewHolder, position: Int) {
        with(holder.jsonItemView) {
            textSize = this@JSONViewAdapter.textSize
            setRightColor(textColor)
            jsonObject?.let { bindJSONObjectData(position, it) }
            jsonArray?.let { bindJSONArrayData(position, it) }
        }
    }

    override fun getItemCount(): Int =
        jsonObject?.let { getJSONObjectCount(it) }
            ?: jsonArray?.let { getJSONArrayCount(it) }
            ?: 0

    fun bindData(jsonStr: String) {
        var any: Any? = null
        try {
            any = JSONTokener(jsonStr).nextValue()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        when (any) {
            is JSONObject -> jsonObject = any
            is JSONArray -> jsonArray = any
            // 如果既不是JSONObject，也不是JSONArray，就抛出IllegalArgumentException异常
            else -> throw IllegalArgumentException("The json string is illegal.")
        }
        notifyDataSetChanged()
    }

    fun bindData(jsonObject: JSONObject) {
        this.jsonObject = jsonObject
        notifyDataSetChanged()
    }

    fun bindData(jsonArray: JSONArray) {
        this.jsonArray = jsonArray
        notifyDataSetChanged()
    }

    /**
     * Get the count of JSONObject.
     * Note that the count of { and } is two.
     * 得到JSONObject的数量。
     * 注意：{和}是2。
     *
     * @param jsonObject The JSONObject.（JSONObject对象。）
     * @return The count of JSONObject.（JSONObject对象的数量。）
     */
    private fun getJSONObjectCount(jsonObject: JSONObject): Int =
        jsonObject.names()?.length()
            ?.let { it + 2 }
            ?: 2

    /**
     * Get the count of JSONArray.
     * Note that the count of { and } is two.
     * 得到JSONArray的数量。
     * 注意：{和}是2。
     *
     * @param jsonArray The JSONArray.（JSONArray对象。）
     * @return The count of JSONArray.（JSONArray对象的数量。）
     */
    private fun getJSONArrayCount(jsonArray: JSONArray): Int =
        jsonArray.length() + 2

    /**
     * Handle json item view styles with a value of type Number.
     * 处理值为Number类型的JsonItemView样式。
     *
     * @param itemView The json item view to be processed.（要处理的JsonItemView对象。）
     * @param value The value of type Number.（Number类型的值。）
     */
    private fun SpannableStringBuilder.handleNumberValue(itemView: JSONItemView, value: Number) =
        with(value) {
            itemView.hideIcon()
            append(toString())
            setSpan(
                ForegroundColorSpan(numberColor),
                0,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    /**
     * Handler json item view styles with a value of type Boolean.
     * 处理值为Boolean类型的JsonItemView样式。
     *
     * @param itemView The json item view to be processed.（要处理的JsonItemView对象。）
     * @param value The value of type Boolean.（Boolean类型的值。）
     */
    private fun SpannableStringBuilder.handleBooleanValue(itemView: JSONItemView, value: Boolean) =
        with(value) {
            itemView.hideIcon()
            append(toString())
            setSpan(
                ForegroundColorSpan(booleanColor),
                0,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    /**
     * Handler json item view styles with a value of type String.
     * 处理值为String类型的JsonItemView样式。
     *
     * @param itemView The json item view to be processed.（要处理的JsonItemView对象。）
     * @param value The value of type String.（String类型的值。）
     */
    private fun SpannableStringBuilder.handleStringValue(itemView: JSONItemView, value: String) =
        with(value) {
            itemView.hideIcon()
            append("\"").append(value).append("\"")
            val totalLen = this@handleStringValue.length
            isUrl(value)
                .yes {
                    // 设置单引号（"）的样式
                    setSpan(
                        ForegroundColorSpan(stringColor),
                        0,
                        1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    // 设置url的样式
                    setSpan(
                        ForegroundColorSpan(urlColor),
                        1,
                        totalLen - 1,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    // 设置单引号（"）的样式
                    setSpan(
                        ForegroundColorSpan(stringColor),
                        totalLen - 1,
                        totalLen,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    itemView.setRightTextClickListener { navigateToBrowser(value) }
                }
                .otherwise {
                    setSpan(
                        ForegroundColorSpan(stringColor),
                        0,
                        totalLen,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
        }

    /**
     * Navigate to browser.
     * 导航到浏览器。
     *
     * @param uriString An RFC 2396-compliant, encoded URI.（一个符合RFC 2396的编码URI。）
     */
    private fun navigateToBrowser(uriString: String) {
        val uri = Uri.parse(uriString)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    /**
     * Handler json item view style with a value of type JSONObject.
     * 处理值为JSONObject类型的JsonIteView样式。
     *
     * @param itemView The json item view to be processed.（要处理的JsonItemView对象。）
     * @param value The value of type JSONObject.（JSONObject类型的值。）
     * @param appendComma Whether to append commas.（是否附加逗号。）
     * @param hierarchy The number of view hierarchies.（View的层次结构数量。）
     */
    private fun SpannableStringBuilder.handleJSONObjectValue(
        itemView: JSONItemView,
        value: JSONObject,
        appendComma: Boolean,
        hierarchy: Int
    ) =
        with(value) {
            itemView.showIcon(true)
            append("Object{...}")
            setSpan(
                ForegroundColorSpan(textColor),
                0,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            itemView.setOnClickListener(
                JsonItemViewClickListener(
                    itemView,
                    value,
                    appendComma,
                    hierarchy + 1
                )
            )
        }

    /**
     * Handler json item view style with a value of type JSONArray.
     * 处理值为JSONArray类型的JsonItemView样式。
     *
     * @param itemView The json item view.（JsonItemView对象。）
     * @param value The value of type JSONArray.（JSONArray类型的值。）
     * @param appendComma Whether to append commas.（是否附加逗号。）
     * @param hierarchy The number of view hierarchies.（View的层次结构数量。）
     */
    private fun SpannableStringBuilder.handleJSONArrayValue(
        itemView: JSONItemView,
        value: JSONArray,
        appendComma: Boolean,
        hierarchy: Int
    ) =
        with(value) {
            itemView.showIcon(true)
            append("Array[").append(value.length().toString()).append("]")
            // 设置Array[的样式
            setSpan(
                ForegroundColorSpan(textColor),
                0,
                // 字符串"Array["的字符数量是6
                6,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // 设置数组数量的样式
            setSpan(
                ForegroundColorSpan(numberColor),
                6,
                length - 1,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            // 设置]的样式
            setSpan(
                ForegroundColorSpan(textColor),
                length - 1,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            itemView.setOnClickListener(
                JsonItemViewClickListener(
                    itemView,
                    value,
                    appendComma,
                    hierarchy + 1
                )
            )
        }

    /**
     * Handler json item view styles with a null value.
     * 处理值为null的JsonItemView样式。
     *
     * @param itemView The json item view to be processed.（要处理的JsonItemView对象。）
     */
    private fun SpannableStringBuilder.handleNullValue(itemView: JSONItemView) =
        with(itemView) {
            itemView.hideIcon()
            append("null")
            setSpan(
                ForegroundColorSpan(nullColor),
                0,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

    /**
     * Handle the styling of the right part of the json item view (i.e., the part that shows the value).
     * 处理JsonItemView右边部分的样式（即展示值的部分）。
     *
     * @param value The value to be displayed in the json item view.（要在JsonItemView展示的value。）
     * @param itemView The json item view to be processed.（要处理的JsonItemView对象。）
     * @param appendComma Whether to append commas.（是否附加逗号。）
     * @param hierarchy The number of view hierarchies.（View的层次结构数量。）
     */
    private fun handleValue(
        value: Any?,
        itemView: JSONItemView,
        appendComma: Boolean,
        hierarchy: Int
    ) {
        itemView.showRight(SpannableStringBuilder().apply {
            when (value) {
                is Number ->
                    // 处理值为Number类型的样式
                    handleNumberValue(itemView, value)
                is Boolean ->
                    // 处理值为Boolean类型的样式
                    handleBooleanValue(itemView, value)
                is String ->
                    // 处理值为String类型的样式
                    handleStringValue(itemView, value)
                is JSONObject ->
                    // 处理值为JSONObject类型的样式
                    handleJSONObjectValue(itemView, value, appendComma, hierarchy)
                is JSONArray ->
                    // 处理值为JSONArray类型的样式
                    handleJSONArrayValue(itemView, value, appendComma, hierarchy)
                else ->
                    // 处理值为null的样式
                    handleNullValue(itemView)
            }
            if (appendComma) append(",")
        })
    }

    /**
     * Handle json item view styles of type JSONObject.
     * 处理JSONObject类型的JsonItemView样式。
     *
     * @param key The key to be displayed in the json item view.（要在JsonItemView展示的key。）
     * @param value The value to be displayed in the json item view.（要在JsonItemView展示的value。）
     * @param appendComma Whether to append commas.（是否附加逗号。）
     * @param hierarchy The number of view hierarchies.（View的层次结构数量。）
     */
    private fun JSONItemView.handleJSONObject(
        key: String,
        value: Any?,
        appendComma: Boolean,
        hierarchy: Int
    ) {
        // 处理JsonItemView左边的样式
        showLeft(SpannableStringBuilder(getHierarchyStr(hierarchy)).apply {
            // "key":
            append("\"").append(key).append("\"").append(":")
            setSpan(ForegroundColorSpan(keyColor), 0, length - 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setSpan(
                ForegroundColorSpan(textColor),
                length - 1,
                length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        })
        // 处理JsonItemView右边的样式
        handleValue(value, this, appendComma, hierarchy)
    }

    /**
     * Handle json item view styles of type JSONArray.
     * 处理JSONArray类型的JsonItemView样式。
     *
     * @param value The value to be displayed in the json item view.（要在JsonItemView展示的value。）
     * @param appendComma Whether to append commas.（是否附加逗号。）
     * @param hierarchy The number of view hierarchies.（View的层次结构数量。）
     */
    private fun JSONItemView.handleJSONArray(
        value: Any?,
        appendComma: Boolean,
        hierarchy: Int
    ) {
        // 处理JsonItemView左边的样式
        showLeft(SpannableStringBuilder(getHierarchyStr(hierarchy)))
        // 处理JsonItemView右边的样式
        handleValue(value, this, appendComma, hierarchy)
    }

    /**
     * Bind the JSONObject data.
     * 绑定JSONObject数据。
     *
     * @param position The position of the item.（该item的位置。）
     * @param jsonObject The JSONObject.（JSONObject对象。）
     */
    private fun JSONItemView.bindJSONObjectData(position: Int, jsonObject: JSONObject) {
        if (position == 0) {
            // 处理第一个item，展示{
            hideLeft()
            hideIcon()
            showRight(SpannableString("{").apply {
                setSpan(ForegroundColorSpan(textColor), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            })
            return
        } else if (position == itemCount - 1) {
            // 处理最后一个item，展示}
            hideLeft()
            hideIcon()
            showRight(SpannableString("}").apply {
                setSpan(ForegroundColorSpan(textColor), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            })
            return
        }
        jsonObject.names()
            ?.let {
                val key = it.optString(position - 1)
                val value = jsonObject.opt(key)
                handleJSONObject(
                    key = key,
                    value = value,
                    // 最后一个数据不用加逗号
                    appendComma = position < itemCount - 2,
                    hierarchy = 1
                )
            }
    }

    /**
     * Bind the JSONArray data.
     * 绑定JSONArray数据。
     *
     * @param position The position of the item.（该item的位置。）
     * @param jsonArray The JSONArray.（JSONArray对象。）
     */
    private fun JSONItemView.bindJSONArrayData(position: Int, jsonArray: JSONArray) {
        if (position == 0) {
            // 处理第一个item，展示[
            hideLeft()
            hideIcon()
            showRight(SpannableString("[").apply {
                setSpan(ForegroundColorSpan(textColor), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            })
            return
        } else if (position == itemCount - 1) {
            // 处理最后一个item，展示]
            hideLeft()
            hideIcon()
            showRight(SpannableString("]").apply {
                setSpan(ForegroundColorSpan(textColor), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            })
            return
        }
        val value = jsonArray.opt(position - 1)
        handleJSONArray(
            value = value,
            // 最后一个数据不用加逗号
            appendComma = position < itemCount - 2,
            hierarchy = 1
        )
    }

    class JsonItemViewHolder(val jsonItemView: JSONItemView) :
        RecyclerView.ViewHolder(jsonItemView) {

        init {
            // 设置item不可回收
            setIsRecyclable(false)
        }

    }

    inner class JsonItemViewClickListener(
        private val itemView: JSONItemView,
        private val value: Any,
        private val appendComma: Boolean,
        private val hierarchy: Int
    ) : View.OnClickListener {

        // 判断是否展开
        private var isExpanded = false

        // 判断是否为JSONObject对象
        private val isJsonObject
            get() = value is JSONObject

        override fun onClick(v: View?) {
            // 如果itemView的子View数量是1，就证明这是第一次展开
            (itemView.childCount == 1)
                .yes { performFirstExpand() }
                .otherwise { performClick() }
        }

        /**
         * The first time the view corresponding to a JSONObject or JSONArray is expanded.
         * 第一次展开JSONObject或者JSONArray对应的itemView。
         */
        private fun performFirstExpand() {
            isExpanded = true
            itemView.showIcon(false)
            itemView.tag = itemView.getRightText()
            itemView.showRight(if (isJsonObject) "{" else "[")

            // 展开该层级以下的视图
            val array: JSONArray? =
                if (isJsonObject) (value as JSONObject).names() else value as JSONArray
            val length = array?.length() ?: 0
            for (i in 0 until length) {
                itemView.addViewNoInvalidate(JSONItemView(itemView.context).apply {
                    textSize = this@JSONViewAdapter.textSize
                    setRightColor(textColor)
                    val childValue = array?.opt(i)
                    isJsonObject
                        .yes {
                            handleJSONObject(
                                key = childValue as String,
                                value = (value as JSONObject)[childValue],
                                appendComma = i < length - 1,
                                hierarchy = hierarchy
                            )
                        }
                        .otherwise {
                            handleJSONArray(
                                value = childValue,
                                appendComma = i < length - 1,
                                hierarchy = hierarchy
                            )
                        }
                })
            }
            // 展示该层级最后的一个视图
            itemView.addViewNoInvalidate(JSONItemView(itemView.context).apply {
                textSize = this@JSONViewAdapter.textSize
                setRightColor(textColor)
                showRight(
                    StringBuilder(getHierarchyStr(hierarchy - 1))
                        .append(if (isJsonObject) "}" else "]")
                        .append(if (appendComma) "," else "")
                )
            })
            // 重绘itemView
            itemView.requestLayout()
            itemView.invalidate()
        }

        /**
         * Click to expand or collapse.
         * 点击后展开或者收缩。
         */
        private fun performClick() {
            itemView.showIcon(isExpanded)
            val rightText = itemView.getRightText()
            itemView.showRight(itemView.tag as CharSequence)
            itemView.tag = rightText
            for (i in 1 until itemView.childCount) {
                // 如果展开的话，就把子View都设成可见状态，否则就设为隐藏状态
                itemView.getChildAt(i).visibility = if (isExpanded) View.GONE else View.VISIBLE
            }
            isExpanded = !isExpanded
        }
    }

}