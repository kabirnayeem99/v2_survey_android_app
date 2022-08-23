package io.github.kabirnayeem99.v2_survey.data.service.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.AnsweredSurveyEntity
import io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.converter.FileConverter
import io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.converter.MultipleChoiceConverter

@Database(
    entities = [AnsweredSurveyEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(FileConverter::class, MultipleChoiceConverter::class)
abstract class SurveyDatabase : RoomDatabase() {
    abstract fun getAnsweredSurveyDao(): AnsweredSurveyDao
}
