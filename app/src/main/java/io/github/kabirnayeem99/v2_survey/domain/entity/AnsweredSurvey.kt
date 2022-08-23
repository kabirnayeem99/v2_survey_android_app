package io.github.kabirnayeem99.v2_survey.domain.entity

import java.io.File

data class AnsweredSurvey(
    val id: Int,
    val question: String,
    val answerText: String? = null,
    val answerNumber: Int? = null,
    val answerImage: File? = null,
    val multipleChoiceAnswer: List<String>? = null
)
