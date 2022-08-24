package io.github.kabirnayeem99.v2_survey.core

import android.app.Application
import android.os.StrictMode
import dagger.hilt.android.HiltAndroidApp
import io.github.kabirnayeem99.v2_survey.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class SurveyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setUpLogging()
        setUpStrictMode()
    }

    private fun setUpLogging() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }


    /**
     * Sets up strict mode for the thread and the VM if App is in debug mode, and version code
     * matches the one we ask for,
     * we are doing this to test if we are running any long running task on the main thread
     */
    private fun setUpStrictMode() {

        val shouldTurnOnStrictMode = BuildConfig.DEBUG && BuildConfig.VERSION_CODE == 0

        if (shouldTurnOnStrictMode) {

            // strict mode for thread
            val threadPolicyBuilder = StrictMode.ThreadPolicy.Builder()
            threadPolicyBuilder.detectAll().penaltyLog()
            val threadPolicy = threadPolicyBuilder.build()
            StrictMode.setThreadPolicy(threadPolicy)

            // strict mode for VM
            val vmPolicyBuilder = StrictMode.VmPolicy.Builder()
            vmPolicyBuilder.detectAll().penaltyLog()
            val vmPolicy = vmPolicyBuilder.build()
            StrictMode.setVmPolicy(vmPolicy)

        }
    }
}