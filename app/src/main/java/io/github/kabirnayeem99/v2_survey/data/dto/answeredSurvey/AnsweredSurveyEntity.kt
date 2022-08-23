package io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.io.File

@Entity(tableName = "answered_survey_entity")
data class AnsweredSurveyEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val question: String,
    @SerializedName("answer_text")
    val answerText: String,
    @SerializedName("answer_number")
    val answerNumber: Int,
    @SerializedName("answer_file")
    val answerImage: File,
    @SerializedName("multiple_choice_answer")
    val multipleChoiceAnswer: List<String>
)