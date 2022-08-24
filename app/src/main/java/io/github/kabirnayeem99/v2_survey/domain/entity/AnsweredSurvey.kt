package io.github.kabirnayeem99.v2_survey.domain.entity

import java.io.File

/**
 * Answered Survey is the survey that the user has answered
 *
 * @property {Int} id - The id of the question.
 * @property {String} question - The question text
 * @property {String?} answerText - This is the answer to the question if the question is a text
 * question.
 * @property {Int?} answerNumber - The answer to a question if the question is a number
 * question.
 * @property {File?} answerImage - The answer to a question if the question is a image
 * question.
 * @property {List<String>?} multipleChoiceAnswer - This is a list of strings that will be used to
 * store the answers to multiple choice questions.
 */
data class AnsweredSurvey(
    val id: Int,
    val question: String,
    val answerText: String? = null,
    val answerNumber: Int? = null,
    val answerImage: File? = null,
    val multipleChoiceAnswer: List<String>? = null
)