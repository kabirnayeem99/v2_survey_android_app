package io.github.kabirnayeem99.v2_survey.domain.entity

/**
 * Survey is the core entity of the project, which holds individual survey item.
 *
 * @property {Int} id - The unique identifier for the survey.
 * @property {String} question - The question that will be asked to the user.
 * @property {List<String>} options - This is a list of options that the user can choose from.
 * @property {Boolean} isRequired - Whether the survey is required or not.
 * @property {SurveyType} type - This is the type of the survey. It can multiple choice, text input,
 * drop down, check box, number input or camera.
 */
data class Survey(
    val id: Int,
    val question: String,
    val options: List<String>,
    val isRequired: Boolean,
    val type: SurveyType,
)

/**
 * Defines the different types of questions that can be asked in a survey.
 */
enum class SurveyType {
    MULTIPLE_CHOICE,
    TEXT_INPUT,
    DROP_DOWN,
    CHECKBOX,
    NUMBER_INPUT,
    CAMERA,
}

