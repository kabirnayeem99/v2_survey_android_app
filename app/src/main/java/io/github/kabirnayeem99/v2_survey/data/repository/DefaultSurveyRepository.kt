package io.github.kabirnayeem99.v2_survey.data.repository

import io.github.kabirnayeem99.v2_survey.data.dataSource.SurveyLocalDataSource
import io.github.kabirnayeem99.v2_survey.data.dataSource.SurveyRemoteDataSource
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import io.github.kabirnayeem99.v2_survey.domain.repository.SurveyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class DefaultSurveyRepository
@Inject constructor(
    private val remoteDataSource: SurveyRemoteDataSource,
    private val localDataSource: SurveyLocalDataSource,
) :
    SurveyRepository {

    override suspend fun getSurveyList(): Flow<Result<List<Survey>>> {
        return flow {
            remoteDataSource.getSurveyList()
                .also { surveyList ->
                    emit(Result.success(surveyList))
                }
        }.catch { e ->
            emit(Result.failure(e))
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun saveSurvey(id: Long, answers: List<AnsweredSurvey>): Result<Long> {
        return try {
            localDataSource.saveAnswers(id, answers)
            Result.success(id)
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure(e)
        }
    }
}