package io.github.kabirnayeem99.v2_survey.presentation.common

/**
 * UserMessage used to show a stack of messages to the user one after another, recognising and differentiating
 * them by their unique ID.
 *
 * @property {Long} id - The unique identifier of the message.
 * @property {String} message - This is the message that the user will see.
 */
data class UserMessage(val id: Long, val message: String)