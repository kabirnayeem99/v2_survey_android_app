package io.github.kabirnayeem99.v2_survey.presentation.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.kabirnayeem99.v2_survey.R
import io.github.kabirnayeem99.v2_survey.core.ktx.bounce
import io.github.kabirnayeem99.v2_survey.databinding.FragmentSurveyBinding
import io.github.kabirnayeem99.v2_survey.domain.entity.SurveyType
import io.github.kabirnayeem99.v2_survey.presentation.common.DialogLoading
import kotlinx.coroutines.launch
import timber.log.Timber


@AndroidEntryPoint
class SurveyFragment : Fragment() {

    private val viewModel: SurveyViewModel by viewModels()

    private var _binding: FragmentSurveyBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { findNavController() }
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
                tvQuestionNo.text = "Q. ${currentSurveyIndex + 1}"
                setUpDropdown(selectedSurvey.type, selectedSurvey.options)
                manageVisibility(uiState.selectedSurvey.type)

                if (isSurveyAtEnd) btnNext.text = getString(R.string.label_finish)
                if (!isSurveyAtEnd) btnNext.text = getString(R.string.label_next)
            }
        }
    }

    private fun manageVisibility(type: SurveyType) {
        binding.apply {
            if (type == SurveyType.CAMERA) {
                ivCamera.visibility = View.VISIBLE
                spDropdown.visibility = View.INVISIBLE
            } else if (type == SurveyType.DROP_DOWN) {
                spDropdown.visibility = View.VISIBLE
                ivCamera.visibility = View.INVISIBLE
            } else {
                spDropdown.visibility = View.INVISIBLE
                ivCamera.visibility = View.INVISIBLE
            }
        }
    }

    private fun setUpDropdown(type: SurveyType, items: List<String>) {
        if (type != SurveyType.DROP_DOWN) return
        Timber.d("Type is $type\nitems->$items")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
        binding.spDropdown.adapter = adapter
    }

    private fun onNextButtonClick(view: View?) {
        view?.bounce()
        if (!viewModel.uiState.value.isSurveyAtEnd) viewModel.loadNextSurvey()
        else Toast.makeText(context, "You are finished.", Toast.LENGTH_LONG).show()
    }

    private fun onPreviousButtonClick(view: View?) {
        view?.bounce()
        viewModel.loadPrevSurvey()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}