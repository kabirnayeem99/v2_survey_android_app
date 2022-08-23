package io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
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
    val multipleChoiceAnswer: List<String>,
    @SerializedName("survey_id")
    val surveyId: Long,
)

fun AnsweredSurvey.toAnsweredSurveyEntity(surveyId: Long): AnsweredSurveyEntity {
    return AnsweredSurveyEntity(
        id = id,
        question = question,
        answerText = answerText ?: "",
        answerNumber = answerNumber ?: -1,
        answerImage = answerImage ?: File(""),
        multipleChoiceAnswer = multipleChoiceAnswer ?: emptyList(),
        surveyId = surveyId
    )
}

fun AnsweredSurveyEntity.toAnsweredSurvey(): AnsweredSurvey {
    return AnsweredSurvey(
        id,
        question,
        answerText.ifBlank { null },
        if (answerNumber == -1) null else answerNumber,
        if (answerImage.absolutePath.isBlank()) null else answerImage,
        if (multipleChoiceAnswer.isEmpty()) null else multipleChoiceAnswer
    )
}