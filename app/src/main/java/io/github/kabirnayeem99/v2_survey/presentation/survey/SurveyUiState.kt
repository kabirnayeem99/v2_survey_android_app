package io.github.kabirnayeem99.v2_survey.presentation.survey

import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import io.github.kabirnayeem99.v2_survey.domain.entity.SurveyType
import io.github.kabirnayeem99.v2_survey.presentation.common.UserMessage

/**
 * @property {Boolean} isLoading - This is a boolean property that indicates whether the app is loading
 * the surveys or not.
 * @property {List<Survey>} surveys - This is the list of surveys that we will be displaying to the
 * user.
 * @property {Int} currentSurveyIndex - This is the index of the current survey in the list of surveys.
 * @property {Survey} selectedSurvey - This is the current survey that the user is answering.
 * @property {Boolean} isSurveyAtEnd - This is a boolean property that indicates whether the user has
 * reached the end of the survey.
 * @property {Int} progress - This is the progress of the survey. It's a percentage value.
 */
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
    val answers: List<AnsweredSurvey> = emptyList(),
    val isSurveyAtEnd: Boolean = false,
    val progress: Int = 10,
    val isAnswerSaved: Boolean = false,
    val selectedAnswer: AnsweredSurvey? = null,
    val userMessages: List<UserMessage> = emptyList(),
)