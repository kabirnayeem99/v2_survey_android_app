package io.github.kabirnayeem99.v2_survey.data.repository

import io.github.kabirnayeem99.v2_survey.data.dataSource.SurveyLocalDataSource
import io.github.kabirnayeem99.v2_survey.data.dataSource.SurveyRemoteDataSource
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurveyCluster
import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import io.github.kabirnayeem99.v2_survey.domain.repository.SurveyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

class DefaultSurveyRepository
@Inject constructor(
    private val remoteDataSource: SurveyRemoteDataSource,
    private val localDataSource: SurveyLocalDataSource,
) : SurveyRepository {

    override suspend fun getSurveyList(): Flow<Result<List<Survey>>> {
        return flow {
            remoteDataSource.getSurveyList()
                .also { surveyList -> emit(Result.success(surveyList)) }
        }.catch { e ->
            Timber.e(e, "Failed to get survey list from server -> ${e.localizedMessage}")
            emit(Result.failure(e))
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun saveSurvey(id: Long, answers: List<AnsweredSurvey>): Result<Long> {
        return try {
            localDataSource.saveAnswers(id, answers)
            Result.success(id)
        } catch (e: Exception) {
            Timber.e(e, "Failed to save survey for $id -> ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    private val lock = Mutex()
    private val inMemoryPreviousAnswers = mutableListOf<AnsweredSurveyCluster>()

    override suspend fun getPreviouslyAnsweredSurveyAnswers(): Flow<Result<List<AnsweredSurveyCluster>>> {
        return flow {
            val clusters = localDataSource.getPreviouslyAnsweredSurveyAsCluster()
            lock.withLock {
                inMemoryPreviousAnswers.clear()
                inMemoryPreviousAnswers.addAll(clusters)
            }
            emit(Result.success(clusters))
        }.onStart {
            if (inMemoryPreviousAnswers.isNotEmpty())
                lock.withLock { emit(Result.success(inMemoryPreviousAnswers)) }
        }.catch { e ->
            Timber.e(e, "Failed to get survey answers -> ${e.localizedMessage}")
            emit(Result.failure(e))
        }
    }
}