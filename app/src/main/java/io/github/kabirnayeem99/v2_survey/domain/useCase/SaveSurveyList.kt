package io.github.kabirnayeem99.v2_survey.domain.useCase

import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.repository.SurveyRepository
import javax.inject.Inject

class SaveSurveyList @Inject constructor(
    private val repository: SurveyRepository
) {
    suspend operator fun invoke(id: Long, answers: List<AnsweredSurvey>): Result<Long> {
        return repository.saveSurvey(id, answers)
    }
}