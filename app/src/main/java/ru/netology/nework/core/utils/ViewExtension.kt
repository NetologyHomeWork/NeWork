package ru.netology.nework.core.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.Locale

fun View?.hideKeyboard(clearFocus: Boolean = false) {
    this ?: return
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(windowToken, 0)
    if (clearFocus) clearFocus()
}

fun View?.showKeyboard() {
    this ?: return
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.showSoftInput(this, 0)
}

fun EditText.extractUnixTimestampOrNull(): Long? {
    if (this.text.isNullOrBlank()) return null
    val dateFormat = SimpleDateFormat(STANDARD_DATA_TIME_FORMAT, Locale.getDefault())
    return try {
        val date = dateFormat.parse(this.text.toString()) ?: return null
        date.time
    } catch (e: Exception) {
        null
    }
}

fun TextView.extractUnixTimestampOrNull(): Long? {
    if (this.text.isNullOrBlank()) return null
    val dateFormat = SimpleDateFormat(STANDARD_DATA_TIME_FORMAT, Locale.getDefault())
    return try {
        val date = dateFormat.parse(this.text.toString()) ?: return null
        date.time
    } catch (e: Exception) {
        null
    }
}