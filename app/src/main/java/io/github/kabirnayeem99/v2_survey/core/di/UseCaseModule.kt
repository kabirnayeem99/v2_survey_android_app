package io.github.kabirnayeem99.v2_survey.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import io.github.kabirnayeem99.v2_survey.domain.repository.SurveyRepository
import io.github.kabirnayeem99.v2_survey.domain.useCase.GetPreviousSurveyList
import io.github.kabirnayeem99.v2_survey.domain.useCase.GetSurveyList
import io.github.kabirnayeem99.v2_survey.domain.useCase.SaveSurveyList

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {
    @Provides
    fun provideGetSurveyList(repository: SurveyRepository): GetSurveyList {
        return GetSurveyList(repository)
    }

    @Provides
    fun provideSaveSurveyList(repository: SurveyRepository): SaveSurveyList {
        return SaveSurveyList(repository)
    }

    @Provides
    fun provideGetPreviousSurveyList(repository: SurveyRepository): GetPreviousSurveyList {
        return GetPreviousSurveyList(repository)
    }
}