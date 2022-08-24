package io.github.kabirnayeem99.v2_survey.presentation

import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kabirnayeem99.v2_survey.R
import io.github.kabirnayeem99.v2_survey.databinding.ActivityContainerBinding
import io.github.kabirnayeem99.v2_survey.presentation.survey.RC_TAKE_PHOTO
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import timber.log.Timber


@AndroidEntryPoint
class ContainerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContainerBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        configureUi()
        super.onCreate(savedInstanceState)
        binding = ActivityContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpNavigation()
        askForCameraPermission()
    }

    private fun configureUi() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }

    fun askForCameraPermission() {
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(this, RC_TAKE_PHOTO, android.Manifest.permission.CAMERA)
                .setRationale(R.string.rationale_camera_permission)
                .setPositiveButtonText(R.string.rationale_ask_ok)
                .setNegativeButtonText(R.string.rationale_ask_cancel)
                .build()
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onBackPressed() {
        Timber.d("User has pressed on the back button, but we won't let him go back.")
    }


    private fun setUpNavigation() {
        findNavController(R.id.nav_host_fragment_content_main)
    }

}