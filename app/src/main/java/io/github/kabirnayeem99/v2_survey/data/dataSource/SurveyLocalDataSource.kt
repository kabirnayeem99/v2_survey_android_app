package io.github.kabirnayeem99.v2_survey.data.dataSource

import io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.toAnsweredSurvey
import io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.toAnsweredSurveyEntity
import io.github.kabirnayeem99.v2_survey.data.service.LocalPreference
import io.github.kabirnayeem99.v2_survey.data.service.db.AnsweredSurveyDao
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurveyCluster
import kotlinx.coroutines.coroutineScope
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SurveyLocalDataSource @Inject constructor(
    private val surveyDao: AnsweredSurveyDao,
    private val localPreference: LocalPreference,
) {

    /**
     * Returns all the previously answered survey in the local device
     *
     * Gets all the survey ids from the local preference, then for each id, it gets the previously
     * answered survey, then it creates a cluster of the survey id and the list of previously answered
     * survey, then it adds the cluster to a list of clusters, then it returns the list of clusters
     *
     * @return A list of AnsweredSurveyCluster
     */
    suspend fun getPreviouslyAnsweredSurveyAsCluster(): List<AnsweredSurveyCluster> {
        val clusters = mutableListOf<AnsweredSurveyCluster>()
        localPreference.getAllSurveyIds().forEachIndexed { index, id ->
            Timber.d("Querying for $id")
            val answerList = getPreviouslyAnsweredSurvey(surveyId = id)
            Timber.d("Result of $answerList")
            AnsweredSurveyCluster(
                id = index,
                time = Date(id),
                answeredSurveyList = answerList
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
    private suspend fun getPreviouslyAnsweredSurvey(surveyId: Long): List<AnsweredSurvey> {
        return coroutineScope {
            surveyDao.getAnsweredSurveyById(surveyId).map { entity ->
                Timber.d("Entity -> $entity")
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
            Timber.d(answeredSurveyEntities.toString())
            surveyDao.insertAnsweredSurvey(answeredSurveyEntities.map {
                it.copy(id = it.id + id.toInt())
            })
        }
    }
}