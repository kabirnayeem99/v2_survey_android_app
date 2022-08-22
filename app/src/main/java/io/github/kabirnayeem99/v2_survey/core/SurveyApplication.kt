package io.github.kabirnayeem99.v2_survey.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.github.kabirnayeem99.v2_survey.BuildConfig
import timber.log.Timber

@HiltAndroidApp
class SurveyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setUpLogging()
    }

    private fun setUpLogging() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }
}