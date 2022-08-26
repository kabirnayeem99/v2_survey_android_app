package io.github.kabirnayeem99.v2_survey.presentation.onboarding

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kabirnayeem99.v2_survey.R
import kotlinx.coroutines.ExperimentalCoroutinesApi
import launchFragmentInHiltContainer
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@ExperimentalCoroutinesApi
@SmallTest
class OnboardingFragmentTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun does_btnHistory_has_on_click_listener() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())

        launchFragmentInHiltContainer<OnboardingFragment>(
            navHostController = navController,
            action = {
                navController.setGraph(R.navigation.nav_graph)
                assert(
                    view?.findViewById<MaterialButton>(R.id.btn_history)
                        ?.hasOnClickListeners() == true
                )
            })
    }

    @Test
    fun does_btnAction_has_on_click_listener() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.nav_graph)

        launchFragmentInHiltContainer<OnboardingFragment>(
            navHostController = navController,
            action = {
                assert(
                    view?.findViewById<MaterialButton>(R.id.btn_action)
                        ?.hasOnClickListeners() == true
                )
            })
    }

    @Test
    fun does_btnHistory_navigate_to_previous_survey_screen() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        runOnUiThread {
            navController.setGraph(R.navigation.nav_graph)
        }
        launchFragmentInHiltContainer<OnboardingFragment>(
            navHostController = navController,
            action = {
                viewLifecycleOwnerLiveData.observeForever {
                    if (it != null) {
                        view?.findViewById<MaterialButton>(R.id.btn_history)!!.callOnClick()
                        assert(navController.currentDestination!!.id == R.id.previousSurveyFragment)
                    }
                }
            })
    }

    @Test
    fun does_btnAction_navigate_to_survey_screen() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        runOnUiThread {
            navController.setGraph(R.navigation.nav_graph)
        }
        launchFragmentInHiltContainer<OnboardingFragment>(
            navHostController = navController,
            action = {
                viewLifecycleOwnerLiveData.observeForever {
                    if (it != null) {
                        view?.findViewById<MaterialButton>(R.id.btn_action)!!.callOnClick()
                        assert(navController.currentDestination!!.id == R.id.surveyFragment)
                    }
                }
            })
    }

}