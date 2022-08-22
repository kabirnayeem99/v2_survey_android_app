package io.github.kabirnayeem99.v2_survey.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kabirnayeem99.v2_survey.R
import io.github.kabirnayeem99.v2_survey.databinding.ActivityContainerBinding


@AndroidEntryPoint
class ContainerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContainerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        configureUi()
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigation()
    }

    private fun configureUi() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }


    private fun setUpNavigation() {
        findNavController(R.id.nav_host_fragment_content_main)
    }

}