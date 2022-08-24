package io.github.kabirnayeem99.v2_survey.presentation.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import io.github.kabirnayeem99.v2_survey.domain.useCase.GetSurveyList
import io.github.kabirnayeem99.v2_survey.domain.useCase.SaveSurveyList
import io.github.kabirnayeem99.v2_survey.presentation.common.UserMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.io.File
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val getSurveyList: GetSurveyList,
    private val saveSurveyList: SaveSurveyList,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SurveyUiState())
    val uiState = _uiState.asStateFlow()

    private var loadAllSurveysJob: Job? = null

    /**
     * Loads the whole survey with all of its questions and options on the first time
     */
    fun loadAllSurveys() {
        loadAllSurveysJob?.cancel()

        loadAllSurveysJob = viewModelScope.launch(Dispatchers.IO) {
            startLoading()

            val doOnFailure: (e: Throwable) -> Unit = { e ->
                makeUserMessage(exception = e)
                stopLoading()
            }

            val doOnSuccess: (surveys: List<Survey>) -> Unit = { surveys ->
                _uiState.update {
                    val selectedSurvey = surveys[it.currentSurveyIndex]
                    it.copy(
                        isLoading = false,
                        surveys = surveys,
                        selectedSurvey = selectedSurvey,
                        selectedAnswer = findAnswerBasedOnId(selectedSurvey.id)
                    )
                }
            }

            getSurveyList()
                .collect { res -> res.fold(onFailure = doOnFailure, onSuccess = doOnSuccess) }
        }
    }

    private var loadNextSurveyJob: Job? = null

    /**
     * Loads the next survey
     */
    fun loadNextSurvey() {
        loadNextSurveyJob?.cancel()
        loadNextSurveyJob = viewModelScope.launch(Dispatchers.IO) {

            showLoadingForAShortPeriod()

            _uiState.update {
                val index = it.currentSurveyIndex + 1

                if (index == it.surveys.size) {
                    it.copy(isSurveyAtEnd = hasReachedAtTheEnd(index, it))
                } else {

                    val progress = calculateProgress(index, it)
                    val selectedSurvey = it.surveys[index]

                    stopLoading()


                    it.copy(
                        isSurveyAtEnd = hasReachedAtTheEnd(index, it),
                        currentSurveyIndex = index,
                        progress = progress,
                        selectedSurvey = selectedSurvey,
                        selectedAnswer = findAnswerBasedOnId(selectedSurvey.id)
                    )
                }
            }
        }
    }

    private var loadPrevSurveyJob: Job? = null

    /**
     * Loads Previous survey
     */
    fun loadPrevSurvey() {
        loadPrevSurveyJob?.cancel()
        loadPrevSurveyJob = viewModelScope.launch(Dispatchers.IO) {
            showLoadingForAShortPeriod()

            _uiState.update {

                val index = it.currentSurveyIndex - 1

                if (index >= 0) {

                    val progress = calculateProgress(index, it)
                    val selectedSurvey = it.surveys[index]

                    it.copy(
                        currentSurveyIndex = index,
                        progress = progress,
                        isSurveyAtEnd = hasReachedAtTheEnd(index, it),
                        selectedSurvey = selectedSurvey,
                        selectedAnswer = findAnswerBasedOnId(selectedSurvey.id)
                    )
                } else {
                    stopLoading()
                    it.copy(isSurveyAtEnd = hasReachedAtTheEnd(index, it))
                }
            }
        }
    }

    private var answerQuestionJob: Job? = null

    /**
     * Save each question's answer to the UI state, to store state even while going forward
     * or going backward
     *
     * @param ans Any, that will save the answer based on the type of the answer.
     */
    fun answerQuestion(ans: Any) {
        answerQuestionJob?.cancel()
        answerQuestionJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.update {
                val currentList = it.answers.toMutableList()

                val ansList = if (ans is List<*>) ans else null

                val answer = AnsweredSurvey(
                    id = it.selectedSurvey.id,
                    question = it.selectedSurvey.question,
                    answerText = if (ans is String) ans else null,
                    multipleChoiceAnswer = ansList?.map { a -> a.toString() },
                    answerImage = if (ans is File) ans else null
                )
                currentList.add(answer)

                it.copy(answers = currentList.toList())
            }
        }
    }

    private var submitSurveyAnswersJob: Job? = null

    /**
     * Submits survey answers to save the user's overall response
     */
    fun submitSurveyAnswers() {
        submitSurveyAnswersJob?.cancel()
        submitSurveyAnswersJob = viewModelScope.launch {
            showLoadingForAShortPeriod(1200)
            saveSurveyList(Date().time, uiState.value.answers).fold(
                onSuccess = { data ->
                    Timber.d(data.toString())
                    _uiState.update { it.copy(isAnswerSaved = true) }
                },
                onFailure = { e ->
                    makeUserMessage(exception = e)
                    _uiState.update { it.copy(isAnswerSaved = false) }
                }
            )
        }
    }

    /**
     * Find the provided answer based on the question id
     *
     * @param id The id of the question that was answered
     * @return The answer to the question with the given id. It may return null
     * if the user has not answered the question yet
     */
    private fun findAnswerBasedOnId(id: Int): AnsweredSurvey? {
        return try {
            uiState.value.answers.find { a -> a.id == id }
        } catch (e: Exception) {
            makeUserMessage(exception = e)
            null
        }
    }

    /**
     * Checks if the survey has reached at the end or not
     */
    fun hasSurveyReachedEnd() = uiState.value.isSurveyAtEnd

    /**
     * If the current survey is not in the list of answers, then it is required
     *
     * @return if the answer is required or not.
     */
    fun isCurrentSurveyAnswerRequired(): Boolean {
        return try {
            val selectedSurvey = uiState.value.selectedSurvey
            val answer = findAnswerBasedOnId(selectedSurvey.id)
            answer == null
        } catch (e: Exception) {
            makeUserMessage(exception = e)
            false
        }
    }


    /**
     * Starts loading
     */
    private fun startLoading() {
        _uiState.update { it.copy(isLoading = true) }
    }

    /**
     * Stops loading
     */
    private fun stopLoading() {
        _uiState.update { it.copy(isLoading = false) }
    }

    /**
     * Show loading for short period of time
     */
    private suspend fun showLoadingForAShortPeriod(time: Long = 600L) {
        startLoading()
        delay(time)
        stopLoading()
    }

    /**
     * Takes the index of the current survey and the current state of the UI, and returns the
     * progress of the survey
     *
     * @param index The index of the survey in the list of surveys.
     * @param state The current state of the UI.
     */
    private suspend fun calculateProgress(index: Int, state: SurveyUiState) =
        coroutineScope {
            // some time while the number of item is 6, or 8
            // the last item may show incomplete progress, so we are manually
            // setting it to 100% for the last item
            if (index == state.surveys.size - 1) 100
            else ((index.toDouble() / state.surveys.size.toDouble()) * 100).toInt() + 10
        }

    /**
     * Checks if the index of the current survey is the last one in the list
     *
     * @param index The index of the current survey in the list of surveys.
     * @param state The current state of the UI.
     */
    private suspend fun hasReachedAtTheEnd(index: Int, state: SurveyUiState) =
        coroutineScope { index + 1 == state.surveys.size }

    /**
     * Makes a new user message with a unique id and add it to the list of user messages.
     *
     * @param messageText The text of the message to be sent.
     * @return Nothing.
     */
    fun makeUserMessage(messageText: String? = null, exception: Throwable? = null) {

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