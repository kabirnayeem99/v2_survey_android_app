package io.github.kabirnayeem99.v2_survey.domain.useCase

import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurveyCluster
import io.github.kabirnayeem99.v2_survey.domain.repository.SurveyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


class GetPreviousSurveyList @Inject constructor(
    private val repository: SurveyRepository
) {
    /**
     * Use case to get previously answered survey list
     * @return the list of [AnsweredSurveyCluster] wrapped in [Result] as a [Flow]
     */
    suspend operator fun invoke(): Flow<Result<List<AnsweredSurveyCluster>>> {
        return repository.getPreviouslyAnsweredSurveyAnswers()
    }
}