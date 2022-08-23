package io.github.kabirnayeem99.v2_survey.data.dataSource

import io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.toAnsweredSurvey
import io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.toAnsweredSurveyEntity
import io.github.kabirnayeem99.v2_survey.data.service.LocalPreference
import io.github.kabirnayeem99.v2_survey.data.service.db.AnsweredSurveyDao
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurveyCluster
import kotlinx.coroutines.coroutineScope
import java.util.*
import javax.inject.Inject

class SurveyLocalDataSource @Inject constructor(
    private val surveyDao: AnsweredSurveyDao,
    private val localPreference: LocalPreference,
) {

    suspend fun getPreviouslyAnsweredSurveyAsCluster(): List<AnsweredSurveyCluster> {
        val clusters = mutableListOf<AnsweredSurveyCluster>()
        localPreference.getAllSurveyIds().forEachIndexed { index, id ->
            AnsweredSurveyCluster(
                id = index,
                time = Date(id),
                answeredSurveyList = getPreviouslyAnsweredSurvey(surveyId = id)
            ).also {
                clusters.add(it)
            }
        }
        return clusters.toList()
    }

    /**
     * Returns a list of previously answered surveys
     *
     * @param surveyId Long - The id of the survey we want to get the answers for.
     * @return A list of AnsweredSurvey objects
     */
    suspend fun getPreviouslyAnsweredSurvey(surveyId: Long): List<AnsweredSurvey> {
        return coroutineScope {
            surveyDao.getAnsweredSurveyById(surveyId).map { entity ->
                entity.toAnsweredSurvey()
            }
        }
    }

    /**
     * Surveys that are submitted is saved locally
     *
     * @param id Long - The id of the survey that the answers are for.
     * @param answers List<AnsweredSurvey>
     */
    suspend fun saveAnswers(id: Long, answers: List<AnsweredSurvey>) {
        coroutineScope {
            localPreference.saveSurveyId(id)
            val answeredSurveyEntities = answers.map { it.toAnsweredSurveyEntity(id) }
            surveyDao.insertAnsweredSurvey(answeredSurveyEntities)
        }
    }
}