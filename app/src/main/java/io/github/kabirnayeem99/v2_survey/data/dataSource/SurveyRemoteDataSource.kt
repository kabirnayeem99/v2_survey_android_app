package io.github.kabirnayeem99.v2_survey.data.dataSource

import io.github.kabirnayeem99.v2_survey.data.dto.getSurvey.toSurveyList
import io.github.kabirnayeem99.v2_survey.data.service.SurveyApiService
import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import javax.inject.Inject

class SurveyRemoteDataSource @Inject constructor(private val apiService: SurveyApiService) {
    /**
     * Makes a network call to get a list of survey questions, and if it fails, it
     * throws an exception
     *
     * @return A list of Survey.
     */
    suspend fun getSurveyList(): List<Survey> {
        val surveyList = apiService.getSurvey().body()?.toSurveyList() ?: emptyList()
        if (surveyList.isEmpty()) throw Exception("Could not find any survey questions.")
        return surveyList
    }
}