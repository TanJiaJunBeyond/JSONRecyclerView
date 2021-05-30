package com.tanjiajun.jsonrecyclerview.utils

/**
 * Created by TanJiaJun on 2021/2/26.
 */
sealed class BooleanExt<out T> {
    class TransferData<out T>(val data: T) : BooleanExt<T>()
    object Otherwise : BooleanExt<Nothing>()
}

inline fun <T> Boolean.yes(block: () -> T): BooleanExt<T> =
    when {
        this -> BooleanExt.TransferData(block.invoke())
        else -> BooleanExt.Otherwise
    }

inline fun <T> BooleanExt<T>.otherwise(block: () -> T): T =
    when (this) {
        is BooleanExt.Otherwise -> block()
        is BooleanExt.TransferData -> data
    }