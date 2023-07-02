package ru.netology.nework.core.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

fun View?.hideKeyboard(clearFocus: Boolean = false) {
    this ?: return
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(windowToken, 0)
    if (clearFocus) clearFocus()
}