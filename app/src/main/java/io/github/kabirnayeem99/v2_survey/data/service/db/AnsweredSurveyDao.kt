package io.github.kabirnayeem99.v2_survey.data.service.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.AnsweredSurveyEntity

@Dao
interface AnsweredSurveyDao {

    /**
     * Inserts a list of AnsweredSurveyEntity objects into the database
     *
     * @param surveys List<AnsweredSurveyEntity> - The list of surveys to be inserted
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnsweredSurvey(surveys: List<AnsweredSurveyEntity>)

    /**
     * Returns a list of AnsweredSurveyEntity objects that have the surveyId passed in as a parameter.
     *
     * @param surveyId The id of the survey we want to get the answers for.
     */
    @Query("SELECT * FROM answered_survey_entity WHERE surveyId=:surveyId")
    suspend fun getAnsweredSurveyById(surveyId: Long): List<AnsweredSurveyEntity>

}