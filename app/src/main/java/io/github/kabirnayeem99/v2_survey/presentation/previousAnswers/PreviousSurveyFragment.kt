package io.github.kabirnayeem99.v2_survey.presentation.previousAnswers

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
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.kabirnayeem99.v2_survey.core.ktx.bounce
import io.github.kabirnayeem99.v2_survey.databinding.FragmentPreviousSurveyBinding
import io.github.kabirnayeem99.v2_survey.presentation.common.DialogLoading
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PreviousSurveyFragment : Fragment() {

    private var _binding: FragmentPreviousSurveyBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { findNavController() }
    private val loading: DialogLoading by lazy(mode = LazyThreadSafetyMode.NONE) {
        DialogLoading(requireActivity())
    }
    private val viewModel: PreviousSurveyViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviousSurveyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeData()
        setUpViews()
    }

    private fun subscribeData() {
        lifecycleScope.launch {
            viewModel.fetchPreviousSurveyList()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect(::handleUiState)
            }
        }
    }

    private fun handleUiState(uiState: PreviousSurveyUiState) {
        uiState.apply {
            answerClusterAdapter.submitAnswerClusterList(answerClusters)
            binding.tvCount.text = count.toString()
        }
    }

    private val answerClusterAdapter: AnswerClusterAdapter by lazy { AnswerClusterAdapter() }

    private fun setUpViews() {
        binding.apply {
            rvPreviousAnswerCluster.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = answerClusterAdapter
            }
            btnBack.setOnClickListener {
                it?.bounce()
                navController.navigateUp()
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        loading.dismiss()
        _binding = null
    }

}