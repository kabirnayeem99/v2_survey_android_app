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


fun mockAnsweredSurvey(id: Int): AnsweredSurvey {
    return AnsweredSurvey(
        id = id,
        question = "Question Question, QUEstion",
        answerText = "Answer Answer Anser"
    )
}

fun mockAnsweredSurveyList(): List<AnsweredSurvey> {
    val list = mutableListOf<AnsweredSurvey>()
    for (i in 0..10) {
        list.add(mockAnsweredSurvey(i))
    }
    return list.toList()
}