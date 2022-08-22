package io.github.kabirnayeem99.v2_survey.presentation.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kabirnayeem99.v2_survey.R
import io.github.kabirnayeem99.v2_survey.databinding.FragmentSurveyBinding
import io.github.kabirnayeem99.v2_survey.presentation.common.DialogLoading
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SurveyFragment : Fragment() {
    private var _binding: FragmentSurveyBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { findNavController() }
    private val viewModel: SurveyViewModel by viewModels()
    private val loading: DialogLoading by lazy(mode = LazyThreadSafetyMode.NONE) {
        DialogLoading(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurveyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        subscribeToData()
    }

    private fun setUpViews() {
        binding.apply {
            btnNext.setOnClickListener(::onNextButtonClick)
            btnPrevious.setOnClickListener(::onPreviousButtonClick)
        }
    }

    private fun subscribeToData() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadAllSurveys()
                viewModel.uiState.collect(::handleUiState)
            }
        }
    }


    private fun handleUiState(uiState: SurveyUiState) {
        uiState.apply {
            if (isLoading) loading.show() else loading.dismiss()
            binding.apply {
                lpiSurveyProgress.progress = progress
                tvQuestion.text = selectedSurvey.question
                if (isSurveyAtEnd) btnNext.text = getString(R.string.label_finish)
                if (!isSurveyAtEnd) btnNext.text = getString(R.string.label_next)
            }
        }
    }

    private fun onNextButtonClick(view: View?) = viewModel.loadNextSurvey()


    private fun onPreviousButtonClick(view: View?) = viewModel.loadPrevSurvey()


    private fun navigateToSurveyScreen() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}