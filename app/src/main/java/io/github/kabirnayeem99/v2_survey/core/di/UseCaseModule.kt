package io.github.kabirnayeem99.v2_survey.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.kabirnayeem99.v2_survey.domain.repository.SurveyRepository
import io.github.kabirnayeem99.v2_survey.domain.useCase.GetSurvey

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetSurvey(repository: SurveyRepository): GetSurvey {
        return GetSurvey(repository)
    }
}