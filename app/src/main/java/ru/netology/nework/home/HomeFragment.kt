package ru.netology.nework.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import ru.netology.nework.R
import ru.netology.nework.core.utils.viewBinding
import ru.netology.nework.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding<FragmentHomeBinding>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initNavigation()
    }

    private fun initNavigation() {
        val navHost = childFragmentManager.findFragmentById(R.id.home_container) as? NavHostFragment
        val navController = navHost?.navController ?: return
        binding.navView.setupWithNavController(navController)
    }
}