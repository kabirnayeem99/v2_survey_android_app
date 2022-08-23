package io.github.kabirnayeem99.v2_survey.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.kabirnayeem99.v2_survey.data.dataSource.SurveyLocalDataSource
import io.github.kabirnayeem99.v2_survey.data.dataSource.SurveyRemoteDataSource
import io.github.kabirnayeem99.v2_survey.data.service.LocalPreference
import io.github.kabirnayeem99.v2_survey.data.service.SurveyApiService
import io.github.kabirnayeem99.v2_survey.data.service.db.AnsweredSurveyDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    fun provideSurveyRemoteDataSource(apiService: SurveyApiService): SurveyRemoteDataSource {
        return SurveyRemoteDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideLocalPreference(@ApplicationContext context: Context): LocalPreference {
        return LocalPreference(context)
    }

    @Provides
    fun provideSurveyLocalDataSource(
        surveyDao: AnsweredSurveyDao,
        localPreference: LocalPreference
    ): SurveyLocalDataSource {
        return SurveyLocalDataSource(surveyDao, localPreference)
    }
}