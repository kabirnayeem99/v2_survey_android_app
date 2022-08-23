package io.github.kabirnayeem99.v2_survey.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.kabirnayeem99.v2_survey.data.dataSource.SurveyLocalDataSource
import io.github.kabirnayeem99.v2_survey.data.dataSource.SurveyRemoteDataSource
import io.github.kabirnayeem99.v2_survey.data.repository.DefaultSurveyRepository
import io.github.kabirnayeem99.v2_survey.domain.repository.SurveyRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideSurveyRepository(
        remoteDataSource: SurveyRemoteDataSource,
        localDataSource: SurveyLocalDataSource,
    ): SurveyRepository {
        return DefaultSurveyRepository(remoteDataSource, localDataSource)
    }
}