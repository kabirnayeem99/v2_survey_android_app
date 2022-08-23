package io.github.kabirnayeem99.v2_survey.domain.repository

import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurveyCluster
import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import kotlinx.coroutines.flow.Flow

interface SurveyRepository {
    suspend fun getSurveyList(): Flow<Result<List<Survey>>>
    suspend fun saveSurvey(id: Long, answers: List<AnsweredSurvey>): Result<Long>
    suspend fun getPreviouslyAnsweredSurveyAnswers(): Result<List<AnsweredSurveyCluster>>
}