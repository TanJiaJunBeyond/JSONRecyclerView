# JSONRecyclerView
该控件可以**方便调试响应数据**，如下图所示：

![JSONRecyclerViewDemo.gif](https://github.com/TanJiaJunBeyond/ImageRepository/raw/master/JSONRecyclerViewDemo.gif)

控件的样式参考以下这个网站：

[JSON在线解析解析及格式化验证](https://www.json.cn)

**项目的GitHub：**[JSONRecyclerView](https://github.com/TanJiaJunBeyond/JSONRecyclerView)

**项目Demo的GitHub：**[JSONRecyclerViewDemo](https://github.com/TanJiaJunBeyond/JSONRecyclerViewDemo)

# 概述

控件是以**RecyclerView**为基础，文本会呈现相应的颜色以反映对应的类型，如果值的类型为**JSONObject**或者**JSONArray**，该数据对应的视图可以**展开**或者**收缩**，除此之外，文本的**大小**或者**颜色**都可以自定义更改。

文本分为以下**七种类型**：

* **普通文本**：**冒号**、**花括号**、**中括号**、**逗号**、**Object{...}**和**Array[]**。
* **key类型的文本**
* **String类型的文本**
* **Number类型的文本**
* **Boolean类型的文本**
* **url文本**
* **null文本**

默认文本颜色如下所示：

```xml
<color name="default_text_color">#333333</color>
<color name="default_key_color">#92278f</color>
<color name="default_string_color">#3ab54a</color>
<color name="default_number_color">#25aae2</color>
<color name="default_boolean_color">#f98280</color>
<color name="default_url_color">#61d2d6</color>
<color name="default_null_color">#f1592a</color>
```

# 使用方法

使用方法如下所示：

## 导入到项目

通过以下代码导入到你的项目：

```groovy
dependencies {
    implementation 'com.github.TanJiaJunBeyond:JSONRecyclerView:1.0.0'
}
```

## 添加到视图

然后将**JsonRecyclerView**添加到**xml文件**中，代码如下所示：

```xml
<com.tanjiajun.jsonrecyclerview.view.JSONRecyclerView
    android:id="@+id/rv_json"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

或者通过**addView**相关的方法添加到视图中，代码如下所示：

```kotlin
val rvJson = JSONRecyclerView(this)
linearLayout.addView(rvJson)
```

我们可以通过**setStyle**方法改变**样式**，代码如下所示：

```kotlin
rvJson.setStyles(textColor = ContextCompat.getColor(this,R.color.black))
```

## 绑定数据

通过**bindData**方法**绑定数据**，接受**JSON字符串**、**JSONObject**和**JSONArray**三种类型的数据。

当我们数据类型是**JSON字符串**的时候，可以调用以下方法：

```kotlin
// JSONRecyclerView.kt
/**
 * 绑定JSON字符串数据。
 * Bind the json string data.
 *
 * @param jsonString The json string to bind.（要绑定的JSON字符串。）
 */
fun bindData(jsonString: String) =
        adapter.bindData(jsonString)
```

当我们数据类型是**JSONObject**的时候，可以调用以下方法：

```kotlin
// JSONRecyclerView.kt
/**
 * 绑定JSONObject数据。
 * Bind the json object data.
 *
 * @param jsonObject The json object to bind.（要绑定的JSONObject。）
*/
fun bindData(jsonObject: JSONObject) =
        adapter.bindData(jsonObject)	
```

当我们数据类型是**JSONArray**的时候，可以调用以下方法：

```kotlin
// JSONRecyclerView.kt
/**
 * 绑定JSONArray数据。
 * Bind the json array data.
 *
 * @param jsonArray The json array to bind.（要绑定的JSONArray。）
*/
fun bindData(jsonArray: JSONArray) =
        adapter.bindData(jsonArray)
```

# 示例代码

代码如下所示：

```kotlin
package com.tanjiajun.jsonrecyclerviewdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tanjiajun.jsonrecyclerview.view.JSONRecyclerView

/**
 * Created by TanJiaJun on 6/1/21.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<JSONRecyclerView>(R.id.rv_json).bindData(
            "{\n" +
                    "    \"string\":\"string\",\n" +
                    "    \"number\":100,\n" +
                    "    \"boolean\":true,\n" +
                    "    \"url\":\"https://github.com/TanJiaJunBeyond/JSONRecyclerView\",\n" +
                    "    \"JSONObject\":{\n" +
                    "        \"string\":\"string\",\n" +
                    "        \"number\":100,\n" +
                    "        \"boolean\":true\n" +
                    "    },\n" +
                    "    \"JSONArray\":[\n" +
                    "        {\n" +
                    "            \"string\":\"string\",\n" +
                    "            \"number\":100,\n" +
                    "            \"boolean\":true\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}"
        )
    }

}
```

# 核心代码

大部分代码都有**中英文**对应的**注释**，可能有些地方翻译地不太好，请各位见谅哈。

上面也提到了，该控件是以**RecyclerView**为基础，涉及到**JsonItemView**、**JsonViewAdapter**和**JsonRecyclerView**三个类。

## JSONItemView

该类用于**展示每一条数据对应的视图**，用到的**布局文件**是**item_json_view.xml**，它继承**LinearLayout**，有**四个**关键的**变量**，代码如下所示:

```kotlin
// JSONItemView.kt
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
        ivIcon.layoutParams = (ivIcon.layoutParams as LinearLayout.LayoutParams).apply {
            width = size
            height = size
        }
        // 设置右边文本的文字大小
        tvRight.textSize = field
    }
```

* 变量**tvLeft**是**左边**的**TextView**，用于展示**key相关的文本**。

* 变量**ivIcon**是**中间**的**ImageView**，用于展示**展开或者收缩的图标**

* 变量**tvRight**是**右边**的**TextView**，用于展示**value相关的文本**。
* 变量**textSize**是**public**的变量，可以通过该变量改变文本的**大小**，要注意的是，单位是**sp**，无论是赋多大或者多小值，文本的**最小值**是**12sp**，**最大值**是**30sp**。

## JSONViewAdapter

该类用于**处理不同类型的数据和点击相关的逻辑**，它继承**RecyclerView.Adapter**，主要涉及到如下几个关键的方法：

### onBindViewHolder方法

代码如下所示：

```kotlin
// JSONViewAdapter.kt
override fun onBindViewHolder(holder: JSONViewAdapter.JsonItemViewHolder, position: Int) {
    with(holder.jsonItemView) {
        textSize = this@JSONViewAdapter.textSize
        setRightColor(textColor)
        jsonObject?.let { bindJSONObjectData(position, it) }
        jsonArray?.let { bindJSONArrayData(position, it) }
    }
}
```

作用是**将数据绑定到视图**，如果数据类型是**JSONObject**，就调用**bindJSONObjectData**方法，如果数据类型是**JSONArray**，就调用**bindJSONArrayData**方法。

### handleValue方法

代码如下所示：

```kotlin
// JSONViewAdapter.kt
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
```

### onClick方法

如果数据类型是**JSONObject**或者**JSONArray**的话，可以通过**点击**来**展开**或者**收缩**视图，代码如下所示：

如果是**第一次展开**，就调用**performFirstExpand**方法，否则就调用**performClick**方法，代码如下所示：

```kotlin
// JSONViewAdapter.kt
override fun onClick(v: View?) {
    // 如果itemView的子View数量是1，就证明这是第一次展开
    (itemView.childCount == 1)
        .yes { performFirstExpand() }
        .otherwise { performClick() }
}
```

#### performFirstExpand方法

该方法用于**第一次展开JSONObject或者JSONArray对应的itemView**，代码如下所示：

```kotlin
// JSONViewAdapter.kt
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
```

#### performClick方法

该方法用于**点击后展开或者收缩**，代码如下所示：

```kotlin
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
```

如果数据类型是**url**的话，可以通过**点击**来打开**浏览器**查看，代码如下所示：

```kotlin
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
```
判断是否是**url**类型的**正则表达式**如下所示，**注释**已经写得很详细，这里就不再赘述：

```kotlin
private val urlPattern: Pattern = Pattern.compile(
    // 验证是否是http://、https://、ftp://、rtsp://、mms://其中一个
    "((http|https|ftp|rtsp|mms)?://)?" +
            // 判断字符是否为FTP地址（ftp://user:password@）
            // 判断字符是否为0到9、小写字母a到z、_、!、~、*、'、(、)、.、&、=、+、$、%、-其中一个，匹配零次或者一次
            "(([0-9a-z_!~*'().&=+\$%-]+: )?" +
            // 判断字符是否为0到9、小写字母a到z、_、!、~、*、'、(、)、.、&、=、+、$、%、-其中一个，匹配一次或者多次
            "[0-9a-z_!~*'().&=+\$%-]+" +
            // @
            "@)?" +
            // 判断字符是否为IP地址，例子：192.168.255.255
            // 判断字符是否匹配1+[0到9，匹配两次]，例如：192
            "((1\\d{2}" +
            // 或者
            "|" +
            // 判断字符是否匹配2+[0到4，匹配一次]+[0到9，匹配一次]，例如：225
            "2[0-4]\\d" +
            // 或者
            "|" +
            // 判断字符是否匹配25+[0到5，匹配一次]，例如：255
            "25[0-5]" +
            // 或者
            "|" +
            // 判断字符是否匹配[1到9，匹配一次]+[0到9，匹配一次]，例如：25
            "[1-9]\\d" +
            // 或者
            "|" +
            // 判断字符是否匹配1到9，匹配一次，例如：5
            "[1-9])" +
            // 判断字符是否匹配\.(1\d{2}|2[0-4]\d|25[0-5]|[1-9]\d|\d)，匹配三次
            "(\\.(" +
            // 判断字符是否匹配1+[0到9，匹配两次]，例如：192
            "1\\d{2}" +
            // 或者
            "|" +
            // 判断字符是否匹配2+[0到4，匹配一次]+[0到9，匹配一次]，例如：225
            "2[0-4]\\d" +
            // 或者
            "|" +
            // 判断字符是否匹配25+[0到5，匹配一次]，例如：255
            "25[0-5]" +
            // 或者
            "|" +
            // 判断字符是否匹配[1到9]+[0到9]，例如：25
            "[1-9]\\d" +
            // 或者
            "|" +
            // 判断字符是否匹配0到9，匹配一次，例如：5
            "\\d))" +
            // 匹配三次
            "{3}" +
            // 或者
            "|" +
            // 判断字符是否为域名（Domain Name）
            // 三级域名或者以上，判断字符是否为0到9、小写字母a到z、_、!、~、*、'、(、)、-其中一个，匹配零次或者多次，然后加上.，例如：www.
            "([0-9a-z_!~*'()-]+\\.)*" +
            // 二级域名，长度不能超过63个字符，先判断第一个字符是否为0到9、小写字母a到z其中一个，匹配一次，然后判断第二个字符是否为0到9、小写字母a到z、-其中一个，最多匹配61次，这两个字符匹配零次或者一次，最后判断第三个字符是否为0到9、小写字母a到z其中一个，然后加上.
            "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]" +
            // 顶级域名，判断字符是否为小写字母a到z其中一个，匹配最少两次、最多六次，例如：.com、.cn
            "\\.[a-z]{2,6})" +
            // 端口号，判断字符是否匹配:+[0到9，匹配最少一次、最多四次]，匹配零次或者一次
            "(:[0-9]{1,4})?" +
            // 判断字符是否为斜杠（/），匹配零次或者一次，如果没有文件名，就不需要斜杠
            "((/?)|" +
            // 判断字符是否为0到9、小写字母a到z、大写字母A到Z、_、!、~、*、'、(、)、.、;、?、:、@、&、=、+、$、,、%、#、-其中一个，匹配一次或者多次
            "(/[0-9a-zA-Z_!~*'(){}.;?:@&=+\$,%#-]+)+" +
            // 判断字符是否为斜杠（/），匹配零次或者一次
            "/?)\$"
)
```

该**正则表达式**可视化图如下所示：

![UrlRegularExpression.png](https://github.com/TanJiaJunBeyond/ImageRepository/raw/master/UrlRegularExpression.png)

## JSONRecyclerView

该类用于**将要处理的数据以列表的方式展示到视图**，注释写得比较清楚，这里就不再赘述了，代码如下所示：

```kotlin
// JSONRecyclerView.kt
package com.tanjiajun.widget

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tanjiajun.widget.R
import com.tanjiajun.widget.JSONViewAdapter
import com.tanjiajun.widget.DEFAULT_TEXT_SIZE_SP
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by TanJiaJun on 5/31/21.
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
```

# 题外话

介绍一个**将正则表达式可视化的网站**，网址如下：

[Regexper](https://regexper.com)



**我的GitHub：**[TanJiaJunBeyond](https://github.com/TanJiaJunBeyond)

**Android通用框架：**[Android通用框架](https://github.com/TanJiaJunBeyond/AndroidGenericFramework)

**我的掘金：**[谭嘉俊](https://juejin.im/user/593f7b33fe88c2006a37eb9b)

**我的简书：**[谭嘉俊](https://www.jianshu.com/u/257511d0c878)

**我的CSDN：**[谭嘉俊](https://blog.csdn.net/qq_20417381)
