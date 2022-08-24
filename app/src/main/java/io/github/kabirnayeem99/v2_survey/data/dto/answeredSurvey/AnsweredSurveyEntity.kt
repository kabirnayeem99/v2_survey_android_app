package io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import timber.log.Timber
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
        try {
            answerText.ifBlank { null }
        } catch (e: Exception) {
            Timber.w("Failed to read answer text -> ${e.localizedMessage}")
            null
        },
        try {
            if (answerNumber == -1) null else answerNumber
        } catch (e: Exception) {
            Timber.w("Failed to read answer number -> ${e.localizedMessage}")
            null
        },
        try {
            Timber.d(answerImage.toString())
            if (!answerImage.canRead()) null else answerImage
        } catch (e: Exception) {
            Timber.w("Failed to read file -> ${e.localizedMessage}")
            null
        },
        try {
            multipleChoiceAnswer.ifEmpty { null }
        } catch (e: Exception) {
            Timber.w("Failed to read multiple choice -> ${e.localizedMessage}")
            null
        }
    )
}