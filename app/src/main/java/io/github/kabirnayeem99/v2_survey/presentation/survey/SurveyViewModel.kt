package io.github.kabirnayeem99.v2_survey.presentation.survey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.kabirnayeem99.v2_survey.domain.useCase.GetSurvey
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
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
            _uiState.update { it.copy(isLoading = true) }
            getSurvey.invoke().collect { res ->
                res.fold(
                    onFailure = { e ->
                        Timber.e(e, e.localizedMessage)
                        _uiState.update { it.copy(isLoading = false) }
                    },
                    onSuccess = { data ->
                        Timber.d("Data -> $data")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                surveys = data,
                                selectedSurvey = data[it.currentSurveyIndex],
                            )
                        }
                    }
                )
            }
        }
    }

    private var loadNextSurveyJob: Job? = null
    fun loadNextSurvey() {
        loadNextSurveyJob?.cancel()
        loadNextSurveyJob = viewModelScope.launch {

            if (uiState.value.currentSurveyIndex >= uiState.value.surveys.size) return@launch

            _uiState.update { it.copy(isLoading = true) }
            delay(600)

            _uiState.update {
                val index = it.currentSurveyIndex + 1

                if (index == it.surveys.size) {
                    it.copy(isSurveyAtEnd = true, isLoading = false)
                } else {
                    val progress =
                        ((index.toDouble() / it.surveys.size.toDouble()) * 100).toInt() + 10
                    val selectedSurvey = it.surveys[index]

                    it.copy(
                        isLoading = false,
                        isSurveyAtEnd = index + 1 == it.surveys.size,
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
            _uiState.update { it.copy(isLoading = true) }
            delay(600)

            _uiState.update {

                val index = it.currentSurveyIndex - 1

                if (index > 0) {

                    val progress =
                        ((index.toDouble() / it.surveys.size.toDouble()) * 100).toInt() + 10
                    val selectedSurvey = it.surveys[index]

                    it.copy(
                        isLoading = false,
                        currentSurveyIndex = index,
                        progress = progress,
                        isSurveyAtEnd = index + 1 == it.surveys.size,
                        selectedSurvey = selectedSurvey,
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        isSurveyAtEnd = index + 1 == it.surveys.size,
                    )
                }
            }
        }
    }

}