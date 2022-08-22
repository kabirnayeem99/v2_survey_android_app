package io.github.kabirnayeem99.v2_survey.data.dto.getSurvey


import com.google.gson.annotations.SerializedName

class GetSurveyApiResponseDto : ArrayList<GetSurveyApiResponseDto.SurveyItemDto>() {
    data class SurveyItemDto(
        @SerializedName("id")
        val id: Int? = -1, // 1
        @SerializedName("options")
        val options: List<Option?>? = emptyList(),
        @SerializedName("question")
        val question: String? = "", // How are you ?
        @SerializedName("referTo")
        val referTo: Any? = 0, // 6
        @SerializedName("required")
        val required: Boolean? = false, // true
        @SerializedName("type")
        val type: String? = "" // multipleChoice
    ) {
        data class Option(
            @SerializedName("referTo")
            val referTo: Int? = 2, // 2
            @SerializedName("value")
            val value: String? = ""// Very Good
        )
    }
}