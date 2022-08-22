package io.github.kabirnayeem99.v2_survey.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.kabirnayeem99.v2_survey.core.utility.ConnectivityUtility

@Module
@InstallIn(SingletonComponent::class)
object UtilityModule {
    @Provides
    fun provideConnectivityUtility(@ApplicationContext context: Context): ConnectivityUtility {
        return ConnectivityUtility(context)
    }
}