package ru.netology.nework.core.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory<VM : ViewModel>(
    private val viewModelCreator: () -> VM
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return viewModelCreator.invoke() as T
    }
}

inline fun <reified VM : ViewModel> Fragment.assistedViewModels(
    noinline creator: () -> VM
): Lazy<VM> {
    return viewModels { ViewModelFactory(creator) }
}