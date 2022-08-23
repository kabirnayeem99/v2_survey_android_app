package io.github.kabirnayeem99.v2_survey.presentation.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import io.github.kabirnayeem99.v2_survey.domain.useCase.GetSurvey
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val getSurvey: GetSurvey
) : ViewModel() {
    private val _uiState = MutableStateFlow(SurveyUiState())
    val uiState = _uiState.asStateFlow()

    private var loadAllSurveysJob: Job? = null
    fun loadAllSurveys() {
        loadAllSurveysJob?.cancel()

        loadAllSurveysJob = viewModelScope.launch {
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

            getSurvey()
                .collect { res -> res.fold(onFailure = doOnFailure, onSuccess = doOnSuccess) }
        }
    }

    private var loadNextSurveyJob: Job? = null
    fun loadNextSurvey() {
        loadNextSurveyJob?.cancel()
        loadNextSurveyJob = viewModelScope.launch {

            startLoading()

            delay(600)

            _uiState.update {
                val index = it.currentSurveyIndex + 1

                if (index == it.surveys.size) {
                    stopLoading()
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
        loadPrevSurveyJob = viewModelScope.launch {
            startLoading()
            delay(600)

            _uiState.update {

                val index = it.currentSurveyIndex - 1

                if (index >= 0) {

                    val progress = calculateProgress(index, it)
                    val selectedSurvey = it.surveys[index]

                    stopLoading()

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

                val ansText = if (ans is String) ans else null
                val ansList = if (ans is List<*>) ans else null
                val ansFile = if (ans is File) ans else null

                val answer = AnsweredSurvey(
                    id = it.selectedSurvey.id,
                    question = it.selectedSurvey.question,
                    answerText = ansText,
                    multipleChoiceAnswer = ansList?.map { a -> a.toString() },
                    answerImage = ansFile
                )
                currentList.add(answer)
                Timber.d("current answer list -> $currentList")
                it.copy(answers = currentList.toList())
            }
        }
    }

    /**
     * If the current survey is not in the list of answers, then it is required
     *
     * @return if the answer is required or not.
     */
    fun isCurrentSurveyAnswerRequired(): Boolean {
        return try {
            val selectedSurvey = uiState.value.selectedSurvey
            val answer = uiState.value.answers.find { it.id == selectedSurvey.id }
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
     * Takes the index of the current survey and the current state of the UI, and returns the
     * progress of the survey
     *
     * @param index The index of the survey in the list of surveys.
     * @param state The current state of the UI.
     */
    private fun calculateProgress(
        index: Int,
        state: SurveyUiState
    ) = ((index.toDouble() / state.surveys.size.toDouble()) * 100).toInt() + 10

    /**
     * Checks if the index of the current survey is the last one in the list
     *
     * @param index The index of the current survey in the list of surveys.
     * @param state The current state of the UI.
     */
    private fun hasReachedAtTheEnd(index: Int, state: SurveyUiState) =
        index + 1 == state.surveys.size

}