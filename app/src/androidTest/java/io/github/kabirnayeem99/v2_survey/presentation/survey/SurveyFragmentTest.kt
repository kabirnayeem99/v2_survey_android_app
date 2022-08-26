package io.github.kabirnayeem99.v2_survey.presentation.survey

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kabirnayeem99.v2_survey.R
import io.github.kabirnayeem99.v2_survey.data.repository.MockSurveyRepository
import io.github.kabirnayeem99.v2_survey.domain.repository.SurveyRepository
import io.github.kabirnayeem99.v2_survey.domain.useCase.GetSurveyList
import io.github.kabirnayeem99.v2_survey.domain.useCase.SaveSurveyList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import launchFragmentInHiltContainer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@ExperimentalCoroutinesApi
@SmallTest
class SurveyFragmentTest {


    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    private var repository: SurveyRepository? = null
    private var getSurveyList: GetSurveyList? = null
    private var saveSurveyList: SaveSurveyList? = null
    private var viewModel: SurveyViewModel? = null

    @Before
    fun initialise() {
        repository = MockSurveyRepository()
        getSurveyList = GetSurveyList(repository!!)
        saveSurveyList = SaveSurveyList(repository!!)
        viewModel = SurveyViewModel(getSurveyList!!, saveSurveyList!!)
    }

    @After
    fun dispose() {
        repository = null
        getSurveyList = null
        saveSurveyList = null
        viewModel = null
    }

    @Test
    fun does_btnNext_has_on_click_listener() {
        launchFragmentInHiltContainer<SurveyFragment>(
            action = {
                assert(
                    view?.findViewById<MaterialButton>(R.id.btn_next)
                        ?.hasOnClickListeners() == true
                )
            },
        )
    }

    @Test
    fun does_btnPrevious_has_on_click_listener() {
        launchFragmentInHiltContainer<SurveyFragment>(
            action = {
                assert(
                    view?.findViewById<MaterialButton>(R.id.btn_previous)
                        ?.hasOnClickListeners() == true
                )
            },
        )
    }


}