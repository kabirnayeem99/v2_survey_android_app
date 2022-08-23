package io.github.kabirnayeem99.v2_survey.presentation.previousAnswers

import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurveyCluster
import io.github.kabirnayeem99.v2_survey.presentation.common.UserMessage

data class PreviousSurveyUiState(
    val answerClusters: List<AnsweredSurveyCluster> = emptyList(),
    val count: Int = 0,
    val userMessages: List<UserMessage> = emptyList(),
)