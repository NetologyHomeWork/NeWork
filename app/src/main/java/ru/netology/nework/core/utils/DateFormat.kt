package ru.netology.nework.core.utils

import java.text.SimpleDateFormat
import java.util.*

const val ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm"
const val DATA_TIME_FORMAT = "dd.MM.yyyy HH:mm"

fun String.dateFormat(pattern: String = ISO_8601_FORMAT, outputFormat: String = DATA_TIME_FORMAT): String {
    return try {
        val format = SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
        SimpleDateFormat(outputFormat, Locale.getDefault()).format(format as Date)
    } catch (e: Exception) {
        ""
    }
}
