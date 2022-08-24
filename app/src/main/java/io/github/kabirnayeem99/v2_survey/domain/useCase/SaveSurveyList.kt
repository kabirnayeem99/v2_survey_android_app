package io.github.kabirnayeem99.v2_survey.domain.useCase

import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.repository.SurveyRepository
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SaveSurveyList @Inject constructor(private val repository: SurveyRepository) {

    /**
     * Saves all the answers of the survey in the local DB.
     *
     * @param id The time at which the survey was taken, also servers as a unique ID.
     * @param answers that the users has answered in a single survey
     * @return the id wrapped in a [Result] to indicate whether the save was successful or not
     */
    suspend operator fun invoke(id: Long, answers: List<AnsweredSurvey>): Result<Long> {
        Timber.d("${Date(id)} -> Saving survey -> $answers")
        if (answers.isEmpty()) return Result.failure(Exception("No answers were provided."))
        if (id == 0L) return Result.failure(Exception("Time is wrong on user's phone."))
        return repository.saveSurvey(id, answers)
    }
}