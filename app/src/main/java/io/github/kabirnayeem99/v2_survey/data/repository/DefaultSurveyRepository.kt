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
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class DefaultSurveyRepository
@Inject constructor(
    private val remoteDataSource: SurveyRemoteDataSource,
    private val localDataSource: SurveyLocalDataSource,
) : SurveyRepository {

    /**
     * Gets the survey from the server
     *
     * @return the list of survey questions
     */
    override suspend fun getSurveyList(): Flow<Result<List<Survey>>> {
        return flow {
            remoteDataSource.getSurveyList()
                .also { surveyList -> emit(Result.success(surveyList)) }
        }.catch { e ->
            Timber.e(e, "Failed to get survey list from server -> ${e.localizedMessage}")
            emit(Result.failure(e))
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Saves the answered survey to the local database.
     *
     * @param id Long - The id of the survey
     * @param answers list of answers of a specific survey
     * @return returns a [Result] object with the id of the survey
     */
    override suspend fun saveSurvey(id: Long, answers: List<AnsweredSurvey>): Result<Long> {
        return withContext(Dispatchers.IO) {
            try {
                localDataSource.saveAnswers(id, answers)
                Result.success(id)
            } catch (e: Exception) {
                Timber.e(e, "Failed to save survey for id $id -> ${e.localizedMessage}")
                Result.failure(e)
            }
        }
    }

    // mutex makes sure that the data is thread safe
    private val lock = Mutex()
    private val inMemoryPreviousAnswers = mutableListOf<AnsweredSurveyCluster>()

    /**
     * Fetches the previously answered survey answers from the local database
     *
     * @return List of previously answered survey questions.
     */
    override suspend fun getPreviouslyAnsweredSurveyAnswers(): Flow<Result<List<AnsweredSurveyCluster>>> {
        return flow {
            val clusters = localDataSource.getPreviouslyAnsweredSurveyAsCluster()
            // caching in the memory for faster access
            lock.withLock {
                inMemoryPreviousAnswers.clear()
                inMemoryPreviousAnswers.addAll(clusters)
            }
            emit(Result.success(clusters))
        }.onStart {
            // before loading from the database, show existing list in the memory cache
            if (inMemoryPreviousAnswers.isNotEmpty())
                lock.withLock { emit(Result.success(inMemoryPreviousAnswers)) }
        }.catch { e ->
            Timber.e(e, "Failed to get survey answers -> ${e.localizedMessage}")
            emit(Result.failure(e))
        }.flowOn(Dispatchers.IO)
    }
}