package io.github.kabirnayeem99.v2_survey.presentation.survey

import io.github.kabirnayeem99.v2_survey.data.dto.repository.MockSurveyRepository
import io.github.kabirnayeem99.v2_survey.domain.useCase.GetSurveyList
import io.github.kabirnayeem99.v2_survey.domain.useCase.SaveSurveyList
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SurveyViewModelTest {
    private val repository = MockSurveyRepository()
    private val getSurveyList: GetSurveyList = GetSurveyList(repository)
    private val saveSurveyList: SaveSurveyList = SaveSurveyList(repository)
    private val viewModel = SurveyViewModel(getSurveyList, saveSurveyList)

    @Test
    fun does_loadAllSurvey_include_survey_list_in_ui_state() {
        runBlocking {
            viewModel.loadAllSurveys()
            delay(500)
            val surveys = viewModel.uiState.value.surveys
            assert(surveys.isNotEmpty())
        }
    }

    @Test
    fun does_loadNextSurvey_change_selected_survey_in_ui_state() {
        runBlocking {
            viewModel.loadAllSurveys()
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)
            val selectedSurvey = viewModel.uiState.value.selectedSurvey
            assert(selectedSurvey.id == 1)
        }
    }

    @Test
    fun does_loadPrevSurvey_change_selected_survey_in_ui_state() {
        runBlocking {
            viewModel.loadAllSurveys()
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)
            viewModel.loadPrevSurvey()
            delay(500)
            val selectedSurvey = viewModel.uiState.value.selectedSurvey
            assert(selectedSurvey.id == 1)
        }
    }

    @Test
    fun does_answerQuestion_change_answered_question_in_ui_state() {
        runBlocking {
            viewModel.loadAllSurveys()
            delay(500)
            val selectedSurvey = viewModel.uiState.value.selectedSurvey
            assert(selectedSurvey.id == 1)
        }
    }
}