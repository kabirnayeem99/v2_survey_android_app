package io.github.kabirnayeem99.v2_survey.domain.entity

import java.util.*

data class AnsweredSurveyCluster(
    val id: Int,
    val time: Date,
    val answeredSurveyList: List<AnsweredSurvey>,
)
