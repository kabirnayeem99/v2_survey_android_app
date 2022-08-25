package io.github.kabirnayeem99.v2_survey.data.dto.answeredSurvey.converter

import org.junit.Test
import java.io.File

class FileConverterTest {

    private val fileConverter = FileConverter()

    @Test
    fun does_fileToJson_and_jsonToFile_retrieve_same_file() {
        val file = File.createTempFile("test_file_516", ".webp")
        val json = fileConverter.fileToJson(file)
        val newFile = fileConverter.jsonToFile(json)
        assert(file == newFile)
    }

}