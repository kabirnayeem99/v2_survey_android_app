package io.github.kabirnayeem99.v2_survey.data.dto.repository

import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurveyCluster
import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import io.github.kabirnayeem99.v2_survey.domain.entity.SurveyType
import io.github.kabirnayeem99.v2_survey.domain.repository.SurveyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.random.Random

class MockSurveyRepository : SurveyRepository {


    private val surveyList = mutableListOf<Survey>()
    private val answerList = mutableListOf<AnsweredSurveyCluster>()

    init {
        for (index in 0..10) {
            val typeIndex = Random.nextInt(SurveyType.values().size)
            val type = SurveyType.values()[typeIndex]

            Survey(
                id = index,
                question = "Test question?",
                options = if (type == SurveyType.CHECKBOX || type == SurveyType.MULTIPLE_CHOICE
                    || type == SurveyType.DROP_DOWN
                )
                    listOf("Option 1", "Option 2", "Option 3")
                else emptyList(),
                isRequired = 0 % 2 == 0,
                type = type
            ).also {
                surveyList.add(it)
            }
        }
    }

    override suspend fun getSurveyList(): Flow<Result<List<Survey>>> {
        return flow {
            emit(Result.success(surveyList))
        }
    }

    override suspend fun saveSurvey(id: Long, answers: List<AnsweredSurvey>): Result<Long> {
        val d = AnsweredSurveyCluster(id = 0, time = Date(id), answeredSurveyList = answers)
        return Result.success(id)
    }

    override suspend fun getPreviouslyAnsweredSurveyAnswers(): Flow<Result<List<AnsweredSurveyCluster>>> {
        return flow {
            emit(Result.success(answerList))
        }
    }
}