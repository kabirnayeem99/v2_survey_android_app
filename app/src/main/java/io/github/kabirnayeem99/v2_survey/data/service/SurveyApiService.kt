package io.github.kabirnayeem99.v2_survey.data.service

import io.github.kabirnayeem99.v2_survey.data.dto.getSurvey.GetSurveyApiResponseDto
import retrofit2.Response
import retrofit2.http.GET

interface SurveyApiService {

    /**
     * API Endpoint to get a single survey at any given point of time
     *
     *
     * URL: https://example-response.herokuapp.com/getSurvey
     * Method: GET
     * Authentication: No Authentication
     *
     *
     * Response:
     * id: This is a unique id (integer) of the question.
     * question: The value of this key represents the question.
     * type: The value of this key represents the type of the question. There are 6 different types.
     * > multipleChoice: Multiple answer options but can only select one.
     * > dropdown: Same as multipleChoice but the view will be a dropdown.
     * > textInput: Will take text as input.
     * > checkbox: Multiple answer options and allowed to select more than one.
     * > numberInput: Will take only numbers as input.
     * > camera: Will pop up the camera and take an image as input.
     * options: This key contains an array of objects. Every object contains two keys: value (string) and referTo (integer). Here value is the option and referTo is a reference of the next question that should be displayed if this option is being selected. referTo containing “submit” indicates that choosing this option or answering this question (if no options) will result in submitting the whole survey. options will have null value for input type questions like textInput and numberInput and will have the referTo outside.
     * required: This key is a Boolean type determining if the question is required or not. If required it must be answered and vice versa.
     */
    @GET("getSurvey")
    suspend fun getSurvey(): Response<GetSurveyApiResponseDto>
}