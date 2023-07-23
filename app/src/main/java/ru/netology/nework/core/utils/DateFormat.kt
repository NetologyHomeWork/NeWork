package ru.netology.nework.core.utils

import java.text.SimpleDateFormat
import java.util.*

const val ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm"
const val DATA_TIME_FORMAT = "dd.MM.yyyy HH:mm"
const val STANDARD_DATA_TIME_FORMAT = "dd.MM.yyyy"

fun String.dateFormat(pattern: String = ISO_8601_FORMAT, outputFormat: String = DATA_TIME_FORMAT): String {
    return try {
        val format = SimpleDateFormat(pattern, Locale.getDefault()).parse(this)
        SimpleDateFormat(outputFormat, Locale.getDefault()).format(format as Date)
    } catch (e: Exception) {
        ""
    }
}

fun String.toIso8601(pattern: String = STANDARD_DATA_TIME_FORMAT): String {
    return try {
        val inputFormatter = SimpleDateFormat(pattern, Locale.getDefault())
        val outputFormatter = SimpleDateFormat(ISO_8601_FORMAT, Locale.getDefault())
        val date =  inputFormatter.parse(this) ?: throw NullPointerException()
        outputFormatter.format(date)
    } catch (e: Exception) {
        ""
    }
}
