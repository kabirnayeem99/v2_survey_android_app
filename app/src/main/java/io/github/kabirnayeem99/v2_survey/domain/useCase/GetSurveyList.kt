package io.github.kabirnayeem99.v2_survey.domain.useCase

import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import io.github.kabirnayeem99.v2_survey.domain.repository.SurveyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSurveyList @Inject constructor(
    private val repository: SurveyRepository
) {

    /**
     * Returns the list of survey questions
     *
     * @return a list of [Survey] wrapped in [Result] as a [Flow]
     */
    suspend operator fun invoke(): Flow<Result<List<Survey>>> {
        return repository.getSurveyList()
    }
}