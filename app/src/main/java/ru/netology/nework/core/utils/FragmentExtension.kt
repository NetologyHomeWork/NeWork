package ru.netology.nework.core.utils

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import ru.netology.nework.R

fun Fragment.findMainContainerNavController(): NavController {
    val topLevelHost = requireActivity().supportFragmentManager
        .findFragmentById(R.id.main_container) as? NavHostFragment

    return topLevelHost?.navController ?: findNavController()
}