package io.github.kabirnayeem99.v2_survey.data.service

import io.github.kabirnayeem99.v2_survey.data.dto.getSurvey.GetSurveyApiResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface SurveyApiService {
    @GET("getSurvey")
    suspend fun getSurvey(): Response<GetSurveyApiResponseDto>
}