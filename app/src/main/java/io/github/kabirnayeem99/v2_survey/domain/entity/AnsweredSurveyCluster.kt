package io.github.kabirnayeem99.v2_survey.domain.entity

import java.util.*

/**
 * Answer survey cluster is a set of question and answers user has answered in a single survey
 *
 * @property {Int} id - The id of the cluster.
 * @property {Date} time - The time the survey was answered.
 * @property {List<AnsweredSurvey>} list of questions and answers of the specific survey.
 */
data class AnsweredSurveyCluster(
    val id: Int,
    val time: Date,
    val answeredSurveyList: List<AnsweredSurvey>,
)
