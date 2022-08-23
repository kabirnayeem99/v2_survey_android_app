package io.github.kabirnayeem99.v2_survey.data.service.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.AnsweredSurveyEntity

@Dao
interface AnsweredSurveyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnsweredSurvey(surveys: List<AnsweredSurveyEntity>)

    @Query("SELECT * FROM answered_survey_entity WHERE surveyId=:surveyId")
    suspend fun getAnsweredSurveyById(surveyId: Long): List<AnsweredSurveyEntity>

}