package io.github.kabirnayeem99.v2_survey.presentation.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.checkbox.MaterialCheckBox
import dagger.hilt.android.AndroidEntryPoint
import io.github.kabirnayeem99.v2_survey.R
import io.github.kabirnayeem99.v2_survey.core.ktx.bounce
import io.github.kabirnayeem99.v2_survey.core.ktx.showMessage
import io.github.kabirnayeem99.v2_survey.core.utility.fileFromContentUri
import io.github.kabirnayeem99.v2_survey.databinding.FragmentSurveyBinding
import io.github.kabirnayeem99.v2_survey.databinding.LayoutCheckboxItemBinding
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import io.github.kabirnayeem99.v2_survey.domain.entity.Survey
import io.github.kabirnayeem99.v2_survey.domain.entity.SurveyType
import io.github.kabirnayeem99.v2_survey.presentation.ContainerActivity
import io.github.kabirnayeem99.v2_survey.presentation.common.DialogLoading
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber


const val RC_TAKE_PHOTO = 29293;

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

                manageVisibilityBasedOnType(uiState.selectedSurvey.type)

                setUpDropdown(selectedSurvey.type, selectedSurvey.options)
                setUpCheckbox(selectedSurvey.type, selectedSurvey.options)
                setUpMultipleChoice(selectedSurvey.type, selectedSurvey.options)
                setUpCamera(selectedSurvey.type, selectedAnswer)
                setUpNumberInput(selectedSurvey.type, selectedAnswer)
                setUpTextInput(selectedSurvey.type, selectedAnswer)

                if (isSurveyAtEnd) btnNext.text = getString(R.string.label_finish)
                if (!isSurveyAtEnd) btnNext.text = getString(R.string.label_next)

                if (isAnswerSaved) navController.navigateUp()

                userMessages.firstOrNull()?.let { message ->
                    showMessage(message) {
                        viewModel.userMessageShown(it.id)
                    }
                }
            }
        }
    }

    private fun manageVisibilityBasedOnType(type: SurveyType) {
        binding.apply {
            when (type) {
                SurveyType.CAMERA -> {
                    ivCamera.visibility = View.VISIBLE
                    spDropdown.visibility = View.INVISIBLE
                    tietInput.visibility = View.INVISIBLE
                    llCheckbox.visibility = View.INVISIBLE
                    tietNumInput.visibility = View.INVISIBLE
                    rgMultipleChoice.visibility = View.INVISIBLE
                }
                SurveyType.MULTIPLE_CHOICE -> {
                    rgMultipleChoice.visibility = View.VISIBLE
                    spDropdown.visibility = View.INVISIBLE
                    tietInput.visibility = View.INVISIBLE
                    ivCamera.visibility = View.INVISIBLE
                    tietNumInput.visibility = View.INVISIBLE
                    llCheckbox.visibility = View.INVISIBLE
                }
                SurveyType.DROP_DOWN -> {
                    spDropdown.visibility = View.VISIBLE
                    tietInput.visibility = View.INVISIBLE
                    ivCamera.visibility = View.INVISIBLE
                    tietNumInput.visibility = View.INVISIBLE
                    llCheckbox.visibility = View.INVISIBLE
                    rgMultipleChoice.visibility = View.INVISIBLE
                }
                SurveyType.TEXT_INPUT -> {
                    tietInput.visibility = View.VISIBLE
                    spDropdown.visibility = View.INVISIBLE
                    ivCamera.visibility = View.INVISIBLE
                    llCheckbox.visibility = View.INVISIBLE
                    tietNumInput.visibility = View.INVISIBLE
                    rgMultipleChoice.visibility = View.INVISIBLE
                }
                SurveyType.NUMBER_INPUT -> {
                    tietNumInput.visibility = View.VISIBLE
                    tietInput.visibility = View.INVISIBLE
                    spDropdown.visibility = View.INVISIBLE
                    ivCamera.visibility = View.INVISIBLE
                    llCheckbox.visibility = View.INVISIBLE
                    rgMultipleChoice.visibility = View.INVISIBLE
                }
                SurveyType.CHECKBOX -> {
                    llCheckbox.visibility = View.VISIBLE
                    tietNumInput.visibility = View.INVISIBLE
                    tietInput.visibility = View.INVISIBLE
                    spDropdown.visibility = View.INVISIBLE
                    ivCamera.visibility = View.INVISIBLE
                    rgMultipleChoice.visibility = View.INVISIBLE
                }
            }
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            try {
                if (uri != null) {
                    try {
                        binding.ivCamera.setImageURI(uri)
                        val file = fileFromContentUri(requireContext(), uri)
                        viewModel.answerQuestion(file)
                    } catch (e: Exception) {
                        viewModel.makeUserMessage(exception = e)
                    }
                } else {
                    Timber.e("Failed to take image")
                }
            } catch (e: Exception) {
                Timber.e(e, "Failed to pick image -> ${e.localizedMessage}")
                viewModel.makeUserMessage(exception = e)
            }
        }

    private fun setUpNumberInput(type: SurveyType, answer: AnsweredSurvey?) {
        if (type != SurveyType.NUMBER_INPUT) return
        binding.tietNumInput.setText(answer?.answerText ?: "")
    }

    private fun setUpTextInput(type: SurveyType, answer: AnsweredSurvey?) {
        if (type != SurveyType.TEXT_INPUT) return
        binding.tietInput.setText(answer?.answerText ?: "")
    }

    private fun setUpCamera(type: SurveyType, selectedAnswer: AnsweredSurvey?) {
        try {
            if (type != SurveyType.CAMERA) return

            if (viewModel.isCurrentSurveyAnswerRequired()) {
                val icCameraDrawable =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_camera)
                binding.ivCamera.setImageDrawable(icCameraDrawable)
            }

            val hasCameraPermission = EasyPermissions
                .hasPermissions(requireContext(), android.Manifest.permission.CAMERA)

            if (!hasCameraPermission) {
                (activity as ContainerActivity).askForCameraPermission()
                return
            }

            if (selectedAnswer?.answerImage != null)
                binding.ivCamera.setImageURI(selectedAnswer.answerImage.toUri())

            binding.ivCamera.setOnClickListener { startForResult.launch("image/*") }
        } catch (e: Exception) {
            Timber.e(e, "Failed to set up camera -> ${e.localizedMessage}")
            viewModel.makeUserMessage(exception = e)
        }
    }


    private fun setUpCheckbox(type: SurveyType, items: List<String>) {
        try {
            binding.llCheckbox.removeAllViews()

            if (type != SurveyType.CHECKBOX || items.isEmpty()) return

            val answer = viewModel.uiState.value.selectedAnswer?.multipleChoiceAnswer

            items.forEachIndexed { index, option ->
                LayoutCheckboxItemBinding
                    .inflate(layoutInflater, null, false)
                    .apply {
                        val isChecked = answer?.contains(option) ?: false
                        mcbCheckbox.text = option
                        mcbCheckbox.id = index
                        mcbCheckbox.isChecked = isChecked
                        binding.llCheckbox.addView(root)
                    }
            }
        } catch (e: Exception) {
            viewModel.makeUserMessage(exception = e)
        }
    }


    private fun setUpMultipleChoice(type: SurveyType, items: List<String>) {
        binding.rgMultipleChoice.removeAllViews()

        if (type != SurveyType.MULTIPLE_CHOICE || items.isEmpty()) return

        val answer = viewModel.uiState.value.selectedAnswer?.answerText

        try {
            items.forEachIndexed { index, option ->
                val isThisChecked = option == answer
                Timber.d("Is $option checked -> $isThisChecked")
                val btn = RadioButton(requireContext())
                btn.id = index
                btn.text = option
                btn.isChecked = isThisChecked
                binding.rgMultipleChoice.addView(btn)
            }
        } catch (e: Exception) {
            viewModel.makeUserMessage(exception = e)
        }
    }


    private fun setUpDropdown(type: SurveyType, options: List<String>) {
        if (type != SurveyType.DROP_DOWN || options.isEmpty()) return
        binding.spDropdown.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, options)

        val answer = viewModel.uiState.value.selectedAnswer
        var selectedItem = -1
        options.forEachIndexed { index, option ->
            if (answer?.answerText == option) selectedItem = index
        }

        if (selectedItem >= 0) binding.spDropdown.setSelection(selectedItem)
    }


    private fun onNextButtonClick(view: View?) {
        view?.bounce()

        val canLoadNextQuestion = !viewModel.isCurrentSurveyAnswerRequired()
                || submitAndCheckIfAnswerSubmitted()

        if (canLoadNextQuestion) {
            if (viewModel.hasSurveyReachedEnd()) {
                viewModel.submitSurveyAnswers()
            } else viewModel.loadNextSurvey()
        } else viewModel.makeUserMessage(messageText = "The answer is required.")

    }

    private fun submitAndCheckIfAnswerSubmitted(): Boolean {
        val selectedSurvey = viewModel.uiState.value.selectedSurvey

        return when (selectedSurvey.type) {
            SurveyType.MULTIPLE_CHOICE -> getAnswerOfMultipleChoiceSurvey(selectedSurvey)
            SurveyType.NUMBER_INPUT -> getAnswerForNumberInputSurvey()
            SurveyType.TEXT_INPUT -> getAnswerOfTextInputSurvey()
            SurveyType.DROP_DOWN -> getAnswerOfDropDownSurvey()
            SurveyType.CHECKBOX -> getAnswerOfCheckboxes(selectedSurvey)
            SurveyType.CAMERA -> !viewModel.isCurrentSurveyAnswerRequired()
        }
    }

    private fun getAnswerOfMultipleChoiceSurvey(selectedSurvey: Survey): Boolean {
        try {
            val answer = selectedSurvey.options[binding.rgMultipleChoice.checkedRadioButtonId]
            viewModel.answerQuestion(answer)
            return true
        } catch (e: Exception) {
            Timber.e(e, "Failed to get answer of multiple choice survey -> ${e.localizedMessage}")
            viewModel.makeUserMessage(exception = e)
            return false
        }
    }

    private fun getAnswerForNumberInputSurvey(): Boolean {
        try {
            val answer = binding.tietNumInput.text.toString()
            if (answer.isBlank()) return false
            viewModel.answerQuestion(answer)
            return true
        } catch (e: Exception) {
            viewModel.makeUserMessage(exception = e)
            return false
        }
    }

    private fun getAnswerOfTextInputSurvey(): Boolean {
        try {
            val answer = binding.tietInput.text.toString()
            if (answer.isBlank()) return false
            viewModel.answerQuestion(answer)
            return true
        } catch (e: Exception) {
            viewModel.makeUserMessage(exception = e)
            return false
        }
    }

    private fun getAnswerOfDropDownSurvey(): Boolean {
        try {
            val dropDownAnswer = binding.spDropdown.selectedItem.toString()
            if (dropDownAnswer.isEmpty()) return false
            viewModel.answerQuestion(dropDownAnswer)
            return true
        } catch (e: Exception) {
            viewModel.makeUserMessage(exception = e)
            return false
        }
    }

    private fun getAnswerOfCheckboxes(selectedSurvey: Survey): Boolean {
        try {
            val answers = mutableListOf<String>()

            binding.llCheckbox.children.forEachIndexed { index, view ->
                if (view is MaterialCheckBox && view.isChecked)
                    answers.add(selectedSurvey.options[index])
            }

            if (answers.isEmpty()) return false
            viewModel.answerQuestion(answers)

            return true
        } catch (e: Exception) {
            return false
        }
    }

    private fun onPreviousButtonClick(view: View?) {
        view?.bounce()
        viewModel.loadPrevSurvey()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loading.dismiss()
        _binding = null
    }
}