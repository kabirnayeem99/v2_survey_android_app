package io.github.kabirnayeem99.v2_survey.presentation.previousAnswers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kabirnayeem99.v2_survey.domain.useCase.GetPreviousSurveyList
import io.github.kabirnayeem99.v2_survey.presentation.common.UserMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PreviousSurveyViewModel @Inject constructor(
    private val getPreviousSurveyList: GetPreviousSurveyList,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreviousSurveyUiState())
    val uiState = _uiState.asStateFlow()

    private var fetchPreviousSurveyListJob: Job? = null

    /**
     * Fetches a list of previous surveys from the database and updates the UI state with the result
     */
    fun fetchPreviousSurveyList() {
        fetchPreviousSurveyListJob?.cancel()
        fetchPreviousSurveyListJob = viewModelScope.launch {
            getPreviousSurveyList().collect { res ->
                res.fold(
                    onFailure = { e -> makeUserMessage(exception = e) },
                    onSuccess = { answerClusters ->
                        _uiState.update { state ->
                            state.copy(answerClusters = answerClusters, count = answerClusters.size)
                        }
                    }
                )
            }
        }
    }

    /**
     * Makes a new user message with a unique id and add it to the list of user messages.
     *
     * @param messageText The text of the message to be sent.
     * @return Nothing.
     */
    private fun makeUserMessage(messageText: String? = null, exception: Throwable? = null) {

        if (!messageText.isNullOrBlank()) {
            viewModelScope.launch(Dispatchers.IO) {
                _uiState.update {
                    val messages = it.userMessages + UserMessage(
                        id = UUID.randomUUID().mostSignificantBits,
                        message = messageText
                    )
                    it.copy(userMessages = messages)
                }
            }
        }

        if (exception != null) {
            viewModelScope.launch(Dispatchers.IO) {
                _uiState.update {
                    val messages = it.userMessages + UserMessage(
                        id = UUID.randomUUID().mostSignificantBits,
                        message = exception.localizedMessage ?: "Something went wrong."
                    )
                    it.copy(userMessages = messages)
                }
            }
        }
    }

    /**
     * Removes the message after it is shown with the given id from the user messages
     *
     * @param messageId The id of the message that was shown.
     */
    fun userMessageShown(messageId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { currentUiState ->
                val messages = currentUiState.userMessages.filterNot { it.id == messageId }
                currentUiState.copy(userMessages = messages)
            }
        }
    }
}