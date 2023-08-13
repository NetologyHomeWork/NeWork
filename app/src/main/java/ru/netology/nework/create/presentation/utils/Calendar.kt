package ru.netology.nework.create.presentation.utils

import android.content.Context
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.netology.nework.R
import ru.netology.nework.databinding.BottomSheetCalendarBinding

fun showCalendarDialog(
    context: Context,
    data: Long? = null,
    onDateChose: (String) -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    val bottomSheetDialog = BottomSheetDialog(context)
    bottomSheetDialog.apply {
        val binding = BottomSheetCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        data?.let {
            binding.calendarView.date = it
        }
        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            onDateChose.invoke(context.getString(R.string.date, dayOfMonth, month + 1, year))
            dismiss()
        }
        bottomSheetDialog.setOnDismissListener {
            onDismiss?.invoke()
        }

    }.show()
}