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
    return map { i ->
        Survey(
            id = i.id ?: -1,
            question = i.question ?: "",
            options = i.options?.map { o ->
                o?.value ?: ""
            } ?: emptyList(),
            isRequired = i.required ?: false,
            type = getSurveyType(i.type),
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
    if (type == "multipleChoice") return SurveyType.MULTIPLE_CHOICE
    if (type == "textInput") return SurveyType.TEXT_INPUT
    if (type == "dropdown") return SurveyType.DROP_DOWN
    if (type == "checkbox") return SurveyType.CHECKBOX
    if (type == "numberInput") return SurveyType.NUMBER_INPUT
    if (type == "camera") return SurveyType.CAMERA

    return SurveyType.TEXT_INPUT
}