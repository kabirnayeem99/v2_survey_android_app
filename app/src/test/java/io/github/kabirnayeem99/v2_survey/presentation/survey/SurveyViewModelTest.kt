package io.github.kabirnayeem99.v2_survey.presentation.survey

import io.github.kabirnayeem99.v2_survey.data.repository.MockSurveyRepository
import io.github.kabirnayeem99.v2_survey.domain.useCase.GetSurveyList
import io.github.kabirnayeem99.v2_survey.domain.useCase.SaveSurveyList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class SurveyViewModelTest {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val dispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

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

            //    MULTIPLE_CHOICE,
            viewModel.answerQuestion("Option 1")
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)
            assert(viewModel.uiState.value.answers.size == 1)

            //    TEXT_INPUT
            viewModel.answerQuestion("Answer 1")
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)
            assert(viewModel.uiState.value.answers.size == 2)

            //    DROP_DOWN
            viewModel.answerQuestion("Answer 1")
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)
            assert(viewModel.uiState.value.answers.size == 3)

            //    CHECKBOX
            viewModel.answerQuestion(listOf("Option 1", "Option 2"))
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)
            assert(viewModel.uiState.value.answers.size == 4)

            //    NUMBER_INPUT
            viewModel.answerQuestion("100")
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)
            assert(viewModel.uiState.value.answers.size == 5)

            //    CAMERA
            viewModel.answerQuestion(File(""))
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)
            assert(viewModel.uiState.value.answers.size == 6)
        }
    }

    @Test
    fun does_submitSurveyAnswers_change_isAnswerSaved_in_ui_state() {
        runBlocking {
            viewModel.loadAllSurveys()
            delay(500)

            //    MULTIPLE_CHOICE,
            viewModel.answerQuestion("Option 1")
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)


            //    TEXT_INPUT
            viewModel.answerQuestion("Answer 1")
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)

            //    DROP_DOWN
            viewModel.answerQuestion("Answer 1")
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)

            //    CHECKBOX
            viewModel.answerQuestion(listOf("Option 1", "Option 2"))
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)

            //    NUMBER_INPUT
            viewModel.answerQuestion("100")
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)

            //  CAMERA
            viewModel.answerQuestion(File(""))
            delay(500)
            viewModel.loadNextSurvey()
            delay(500)

            viewModel.submitSurveyAnswers()
            delay(1200)
            assert(viewModel.uiState.value.isAnswerSaved)
        }
    }
}