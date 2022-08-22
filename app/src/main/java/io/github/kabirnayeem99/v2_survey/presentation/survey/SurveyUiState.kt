package io.github.kabirnayeem99.v2_survey.presentation.survey

import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import io.github.kabirnayeem99.v2_survey.domain.entity.SurveyType

data class SurveyUiState(
    val isLoading: Boolean = false,
    val surveys: List<Survey> = listOf(),
    val currentSurveyIndex: Int = 0,
    val selectedSurvey: Survey = Survey(
        id = 0,
        question = "",
        options = emptyList(),
        isRequired = false,
        type = SurveyType.CHECKBOX,
    ),
    val isSurveyAtEnd: Boolean = false,
    val progress: Int = 10,
)