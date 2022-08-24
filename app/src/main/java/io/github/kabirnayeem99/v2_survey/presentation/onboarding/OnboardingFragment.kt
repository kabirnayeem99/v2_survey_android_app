package io.github.kabirnayeem99.v2_survey.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kabirnayeem99.v2_survey.R
import io.github.kabirnayeem99.v2_survey.core.ktx.bounce
import io.github.kabirnayeem99.v2_survey.databinding.FragmentOnboardingBinding

@AndroidEntryPoint
class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { findNavController() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        binding.apply {
            btnAction.setOnClickListener(::onTakeTheSurveyButtonClick)
            btnHistory.setOnClickListener(::onHistoryButtonClick)
        }
    }

    private fun onTakeTheSurveyButtonClick(view: View?) {
        view?.bounce()
        navigateToSurveyScreen()
    }

    private fun navigateToSurveyScreen() {
        navController.navigate(R.id.action_OnboardingFragment_to_surveyFragment)
    }

    private fun onHistoryButtonClick(view: View?) {
        view?.bounce()
        navigateToPreviousSurveyScreen()
    }

    private fun navigateToPreviousSurveyScreen() {
        navController.navigate(R.id.action_OnboardingFragment_to_previousSurveyFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}