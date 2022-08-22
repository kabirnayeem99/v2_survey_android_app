package io.github.kabirnayeem99.v2_survey.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.kabirnayeem99.v2_survey.data.dataSource.SurveyRemoteDataSource
import io.github.kabirnayeem99.v2_survey.data.service.SurveyApiService

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    @Provides
    fun provideSurveyRemoteDataSource(apiService: SurveyApiService): SurveyRemoteDataSource {
        return SurveyRemoteDataSource(apiService)
    }
}