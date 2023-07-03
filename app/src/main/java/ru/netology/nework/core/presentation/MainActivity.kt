package ru.netology.nework.core.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.core.utils.observeWhenOnCreated

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private var navController: NavController? = null
    private var graph: NavGraph? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupObserver()
        navController = getRootNavController()
        graph = navController?.navInflater?.inflate(R.navigation.nav_graph)
    }

    override fun onDestroy() {
        super.onDestroy()
        navController = null
        graph = null
    }

    private fun setupObserver() {
        viewModel.commands.observeWhenOnCreated(this) { command ->
            when (command) {
                MainViewModel.MainViewModelCommands.NavigateToLoginScreen -> {
                    graph?.setStartDestination(R.id.nav_auth)
                    graph?.let {
                        navController?.graph = it
                    }
                }
                MainViewModel.MainViewModelCommands.NavigateToHomeScreen -> {
                    graph?.setStartDestination(R.id.nav_main)
                    graph?.let {
                        navController?.graph = it
                    }
                }
            }
        }
    }

    private fun getRootNavController(): NavController? {
        val navHost =
            supportFragmentManager.findFragmentById(R.id.main_container) as? NavHostFragment
        return navHost?.navController
    }
}