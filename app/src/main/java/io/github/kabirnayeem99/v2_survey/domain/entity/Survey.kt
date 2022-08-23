package io.github.kabirnayeem99.v2_survey.domain.entity

import java.util.*

data class Survey(
    val id: Int,
    val question: String,
    val options: List<String>,
    val isRequired: Boolean,
    val type: SurveyType,
)

enum class SurveyType {
    MULTIPLE_CHOICE,
    TEXT_INPUT,
    DROP_DOWN,
    CHECKBOX,
    NUMBER_INPUT,
    CAMERA,
}

fun mockSurvey(id: Int = 0): Survey {
    return Survey(
        id = id,
        question = "Test test test test ${id + 1}?",
        isRequired = Random().nextBoolean(),
        type = SurveyType.CAMERA,
        options = emptyList(),
    )
}

fun mockSurveyList(): List<Survey> {
    val surveys = mutableListOf<Survey>()
    for (i in 0..9) {
        surveys.add(mockSurvey(i))
    }
    return surveys.toList()
}

