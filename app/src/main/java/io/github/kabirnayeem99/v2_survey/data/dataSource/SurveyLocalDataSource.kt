package io.github.kabirnayeem99.v2_survey.data.dataSource

import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.mockAnsweredSurveyList
import kotlinx.coroutines.coroutineScope
import timber.log.Timber

class SurveyLocalDataSource {
    suspend fun getPreviouslyAnsweredSurvey(): List<AnsweredSurvey> {
        return coroutineScope {
            mockAnsweredSurveyList()
        }
    }

    suspend fun saveAnswers(id: Long, answers: List<AnsweredSurvey>) {
        coroutineScope {
            Timber.d("Saving $answers for $id")
        }
    }
}