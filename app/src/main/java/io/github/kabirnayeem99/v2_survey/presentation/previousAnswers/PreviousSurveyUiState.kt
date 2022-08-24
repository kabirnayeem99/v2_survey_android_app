package io.github.kabirnayeem99.v2_survey.presentation.previousAnswers

import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurveyCluster
import io.github.kabirnayeem99.v2_survey.presentation.common.UserMessage

/**
 * @property {List<AnsweredSurveyCluster>} answerClusters - A list of AnsweredSurveyCluster objects.
 * @property {Int} count - The total number of surveys that the user has answered.
 * @property {List<UserMessage>} userMessages - This is a list of UserMessage objects. These are the
 * messages that the user sees.
 */
data class PreviousSurveyUiState(
    val answerClusters: List<AnsweredSurveyCluster> = emptyList(),
    val count: Int = 0,
    val userMessages: List<UserMessage> = emptyList(),
)