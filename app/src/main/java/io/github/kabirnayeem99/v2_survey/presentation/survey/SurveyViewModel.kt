package io.github.kabirnayeem99.v2_survey.presentation.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import io.github.kabirnayeem99.v2_survey.domain.useCase.GetSurveyList
import io.github.kabirnayeem99.v2_survey.domain.useCase.SaveSurveyList
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
    fun loadAllSurveys() {
        loadAllSurveysJob?.cancel()

        loadAllSurveysJob = viewModelScope.launch(Dispatchers.IO) {
            startLoading()

            val doOnFailure: (e: Throwable) -> Unit = { e ->
                Timber.e(e, e.localizedMessage)
                stopLoading()
            }

            val doOnSuccess: (surveys: List<Survey>) -> Unit = { surveys ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        surveys = surveys,
                        selectedSurvey = surveys[it.currentSurveyIndex],
                    )
                }
            }

            getSurveyList()
                .collect { res -> res.fold(onFailure = doOnFailure, onSuccess = doOnSuccess) }
        }
    }

    private var loadNextSurveyJob: Job? = null
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
                        selectedSurvey = selectedSurvey
                    )
                }
            }
        }
    }

    private var loadPrevSurveyJob: Job? = null
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
                    )
                } else {
                    stopLoading()
                    it.copy(isSurveyAtEnd = hasReachedAtTheEnd(index, it))
                }
            }
        }
    }

    private var answerQuestionJob: Job? = null
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
    fun submitSurveyAnswers() {
        submitSurveyAnswersJob?.cancel()
        submitSurveyAnswersJob = viewModelScope.launch {
            showLoadingForAShortPeriod(1200)
            saveSurveyList(Date().time, uiState.value.answers).fold(
                onSuccess = { data ->
                    Timber.d(data.toString())
                },
                onFailure = { e ->
                    Timber.e(e)
                }
            )
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
            val answer = uiState.value.answers.find { a -> a.id == selectedSurvey.id }
            answer == null
        } catch (e: Exception) {
            Timber.e(e, "Failed to determine if the current answer required or not.")
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
        coroutineScope { ((index.toDouble() / state.surveys.size.toDouble()) * 100).toInt() + 10 }

    /**
     * Checks if the index of the current survey is the last one in the list
     *
     * @param index The index of the current survey in the list of surveys.
     * @param state The current state of the UI.
     */
    private suspend fun hasReachedAtTheEnd(index: Int, state: SurveyUiState) =
        coroutineScope { index + 1 == state.surveys.size }

}