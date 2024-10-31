package com.example.playlistmaker.ui.root

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding

class RootActivity : AppCompatActivity(), BottomNavBarShower {

    private lateinit var binding: ActivityRootBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

    }

    override fun showNavbar() {
        binding.bottomNavigationView.visibility = View.VISIBLE
        binding.vTopView.visibility = View.VISIBLE
    }

    override fun hideNavBar() {
        binding.bottomNavigationView.visibility = View.GONE
        binding.vTopView.visibility = View.GONE
    }

}