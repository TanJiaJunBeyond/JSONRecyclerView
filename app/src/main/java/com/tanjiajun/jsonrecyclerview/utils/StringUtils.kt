package com.tanjiajun.jsonrecyclerview.utils

import java.util.regex.Pattern

/**
 * Created by TanJiaJun on 5/29/21.
 */
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

/**
 * Determine if it is a URL.
 * 判断是否为url。
 *
 * @param str The string to be matched.
 * @return Whether the result is a URL.
 */
fun isUrl(str: String) =
    urlPattern.matcher(str).matches()

/**
 * Get hierarchy string.
 * 得到带有层次结构的的字符串。
 *
 * @param hierarchy The hierarchy.
 * @return The hierarchy string.
 */
fun getHierarchyStr(hierarchy: Int): String =
    StringBuilder()
        .apply {
            for (i in 0 until hierarchy) {
                // 空四格
                append("\r\r\r\r")
            }
        }
        .toString()

/**
 * Format the JSON to indent.
 * 对JSON格式化缩进
 *
 * @param jsonStr The json string.
 * @return The json string that have been formatted and indented.
 */
fun jsonFormat(jsonStr: String): String =
    StringBuilder()
        .apply {
            var level = 0
            for (i in jsonStr.indices) {
                val char = jsonStr[i]
                if (level > 0 && '\n' == toString()[length - 1])
                    append(getHierarchyStr(level))
                when (char) {
                    '{', '[' -> {
                        append(char).append("\n")
                        level++
                    }
                    ',' ->
                        append(char).append("\n")
                    '}', ']' -> {
                        append("\n")
                        level--
                        append(getHierarchyStr(level))
                        append(char)
                    }
                    else ->
                        append(char)
                }
            }
        }
        .toString()