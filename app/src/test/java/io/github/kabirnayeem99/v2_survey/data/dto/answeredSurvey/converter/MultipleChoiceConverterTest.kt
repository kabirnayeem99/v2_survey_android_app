package io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.converter

import org.junit.Test

class MultipleChoiceConverterTest {
    private val multipleChoiceConverter = MultipleChoiceConverter()

    @Test
    fun does_multipleChoiceToJson_and_jsonToMultipleChoice_retrieve_same_data() {
        val multipleChoiceOriginal = listOf("Option1", "Option2")
        val json = multipleChoiceConverter.multipleChoiceToJson(multipleChoiceOriginal)
        val multipleChoiceNew = multipleChoiceConverter.jsonToMultipleChoice(json)
        assert(multipleChoiceNew == multipleChoiceOriginal)
    }
}