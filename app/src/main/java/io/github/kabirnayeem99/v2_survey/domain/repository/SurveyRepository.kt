package io.github.kabirnayeem99.v2_survey.domain.repository

import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import kotlinx.coroutines.flow.Flow

interface SurveyRepository {
    suspend fun getSurveyList(): Flow<Result<List<Survey>>>
}