package io.github.kabirnayeem99.v2_survey.presentation.previousAnswers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.kabirnayeem99.v2_survey.databinding.FragmentPreviousSurveyBinding
import io.github.kabirnayeem99.v2_survey.presentation.common.DialogLoading

class PreviousSurveyFragment : Fragment() {

    private var _binding: FragmentPreviousSurveyBinding? = null
    private val binding get() = _binding!!
    private val navController by lazy { findNavController() }
    private val loading: DialogLoading by lazy(mode = LazyThreadSafetyMode.NONE) {
        DialogLoading(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPreviousSurveyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        binding.rvPreviousAnswerCluster.apply {
            layoutManager = LinearLayoutManager(context)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        loading.dismiss()
        _binding = null
    }

}