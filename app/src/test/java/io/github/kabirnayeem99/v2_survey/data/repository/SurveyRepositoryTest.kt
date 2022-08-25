package io.github.kabirnayeem99.v2_survey.data.repository

import io.github.kabirnayeem99.v2_survey.domain.entity.SurveyType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.kotlin.mock
import java.util.*

class SurveyRepositoryTest {

    private val repository = MockSurveyRepository()

    @Test
    fun does_getSurveyList_return_survey() {
        runBlocking {
            val result = repository.getSurveyList().last()
            assert(result.isSuccess)
            assert(result.getOrNull() != null && result.getOrNull()!!.size == SurveyType.values().size)
        }
    }

    @Test
    fun does_saveSurvey_increase_previously_answered_cluster_size() {
        runBlocking {
            val result = repository.saveSurvey(
                id = Date().time,
                answers = listOf(
                    mock(),
                    mock(),
                    mock(),
                    mock(),
                    mock(),
                )
            )
            assert(result.isSuccess)
            delay(500)
            assert(
                (repository.getPreviouslyAnsweredSurveyAnswers().last().getOrNull()?.size ?: 0) == 1
            )
        }
    }


    @Test
    fun does_adding_multiple_survey_result_in_same_size_of_answered_cluster_with_getPreviouslyAnsweredSurveyAnswers() {
        runBlocking {
            val size = 6
            for (index in 0 until size) {
                repository.saveSurvey(
                    id = Date().time,
                    answers = listOf(
                        mock(),
                        mock(),
                        mock(),
                        mock(),
                        mock(),
                    )
                )
                delay(300)
            }
            assert(
                (repository.getPreviouslyAnsweredSurveyAnswers().last().getOrNull()?.size
                    ?: 0) == size
            )
        }
    }

}