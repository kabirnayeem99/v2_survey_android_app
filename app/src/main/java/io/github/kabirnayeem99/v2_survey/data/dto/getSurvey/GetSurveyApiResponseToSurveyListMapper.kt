package io.github.kabirnayeem99.v2_survey.data.dto.getSurvey

import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import io.github.kabirnayeem99.v2_survey.domain.entity.SurveyType


/**
 * Takes a GetSurveyApiResponseDto object, converts it into a list of Survey objects.
 *
 * @receiver GetSurveyApiResponseDto
 * @return List<Survey>
 */
fun GetSurveyApiResponseDto.toSurveyList(): List<Survey> {
    return map { surveyItemDto ->
        Survey(
            id = surveyItemDto.id ?: -1,
            question = surveyItemDto.question ?: "",
            options = surveyItemDto.options?.map { option -> option?.value ?: "" } ?: emptyList(),
            isRequired = surveyItemDto.required ?: false,
            type = getSurveyType(surveyItemDto.type),
        )
    }
}

/**
 * If the type is "multipleChoice", return SurveyType.MULTIPLE_CHOICE, otherwise if the type is
 * "textInput", return SurveyType.TEXT_INPUT, otherwise if the type is "dropdown", return
 * SurveyType.DROP_DOWN, otherwise if the type is "checkbox", return SurveyType.CHECKBOX, otherwise if
 * the type is "numberInput", return SurveyType.NUMBER_INPUT, otherwise if the type is "camera", return
 * SurveyType.CAMERA, otherwise return SurveyType.TEXT_INPUT
 *
 * @param type The type of survey question.
 * @return A SurveyType object
 */
private fun getSurveyType(type: String?): SurveyType {
    return when (type) {
        "multipleChoice" -> SurveyType.MULTIPLE_CHOICE
        "textInput" -> SurveyType.TEXT_INPUT
        "dropdown" -> SurveyType.DROP_DOWN
        "checkbox" -> SurveyType.CHECKBOX
        "numberInput" -> SurveyType.NUMBER_INPUT
        "camera" -> SurveyType.CAMERA
        else -> SurveyType.TEXT_INPUT
    }
}