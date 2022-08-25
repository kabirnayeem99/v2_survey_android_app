package io.github.kabirnayeem99.v2_survey.presentation.survey

import android.net.Uri
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
import io.github.kabirnayeem99.v2_survey.core.ktx.goneOrVisible
import io.github.kabirnayeem99.v2_survey.core.ktx.showUserMessage
import io.github.kabirnayeem99.v2_survey.core.utility.convertContentUriToFile
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


const val RC_TAKE_PHOTO = 29293

@AndroidEntryPoint
class SurveyFragment : Fragment() {

    private val viewModel: SurveyViewModel by viewModels()

    private var _binding: FragmentSurveyBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private val loading: DialogLoading by lazy { DialogLoading(requireActivity()) }

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

    /**
     * Configuring views
     */
    private fun setUpViews() {
        binding.apply {
            btnNext.setOnClickListener(::onNextButtonClick)
            btnPrevious.setOnClickListener(::onPreviousButtonClick)
        }
    }

    private fun subscribeToData() {
        lifecycleScope.launch {
            viewModel.loadAllSurveys()
            repeatOnLifecycle(Lifecycle.State.STARTED) {
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
                tvQuestionNo.text =
                    getString(R.string.label_question_number, currentSurveyIndex + 1)

                manageVisibilityBasedOnType(uiState.selectedSurvey.type)

                setUpDropdown(selectedSurvey.type, selectedSurvey.options)
                setUpCheckbox(selectedSurvey.type, selectedSurvey.options)
                setUpMultipleChoice(selectedSurvey.type, selectedSurvey.options)
                setUpCamera(selectedSurvey.type, selectedAnswer)
                setUpNumberInput(selectedSurvey.type, selectedAnswer)
                setUpTextInput(selectedSurvey.type, selectedAnswer)

                if (isSurveyAtEnd) btnNext.text = getString(R.string.label_finish)
                if (!isSurveyAtEnd) btnNext.text = getString(R.string.label_next)

                // if the answer is saved, we can go back to the onboarding screen
                if (isAnswerSaved) navController.navigateUp()

                // showing the first user in the user message stack
                userMessages.firstOrNull()?.let { message ->
                    showUserMessage(message) { viewModel.userMessageShown(it.id) }
                }
            }
        }
    }

    /**
     * Manages the visibility of the views based on the type of survey.
     *
     * @param type The type of the survey.
     */
    private fun manageVisibilityBasedOnType(type: SurveyType) {
        binding.apply {
            ivCamera.goneOrVisible(type == SurveyType.CAMERA)
            spDropdown.goneOrVisible(type == SurveyType.DROP_DOWN)
            spDropdown.goneOrVisible(type == SurveyType.DROP_DOWN)
            llCheckbox.goneOrVisible(type == SurveyType.CHECKBOX)
            tietInput.goneOrVisible(type == SurveyType.TEXT_INPUT)
            tietNumInput.goneOrVisible(type == SurveyType.NUMBER_INPUT)
            rgMultipleChoice.goneOrVisible(type == SurveyType.MULTIPLE_CHOICE)
        }
    }

    private val startForResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            takeAndConvertTheUriToFile(uri)
        }

    /**
     * Takes the URI from the Gallery intent, sets the image view to the URI, and then converts the
     * URI to a file and saves the file as an answer
     *
     * @param uri The uri of the image taken from the camera.
     */
    private fun takeAndConvertTheUriToFile(uri: Uri?) {
        try {
            binding.ivCamera.setImageURI(uri!!)
            lifecycleScope.launch {
                convertContentUriToFile(requireContext(), uri)
                    ?.let { file -> viewModel.answerQuestion(file) }
            }
        } catch (e: Exception) {
            viewModel.makeUserMessage(exception = e)
        }
    }

    /**
     * If the type of the survey is not a number input, then return. Otherwise, set the text of the
     * number input to the answer text of the answer, or an empty string if the answer is null
     *
     * @param type The type of the survey question.
     * @param answer The answer to the question, if it exists.
     */
    private fun setUpNumberInput(type: SurveyType, answer: AnsweredSurvey?) {
        if (type != SurveyType.NUMBER_INPUT) return
        binding.tietNumInput.setText(answer?.answerText ?: "")
    }

    /**
     * If the type of the survey is not a text input, then return. Otherwise, set the text of the text
     * input to the answer text if it exists, or an empty string if it doesn't
     *
     * @param type The type of survey question.
     * @param answer The answer to the question, if it exists.
     */
    private fun setUpTextInput(type: SurveyType, answer: AnsweredSurvey?) {
        if (type != SurveyType.TEXT_INPUT) return
        binding.tietInput.setText(answer?.answerText ?: "")
    }

    /**
     * Sets up the camera for the user to take a picture, if the survey question is
     * a camera question
     *
     * @param type SurveyType - This is the type of survey. It can be one of the following:
     * @param selectedAnswer AnsweredSurvey?, the already answered question of the user
     */
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
            viewModel.makeUserMessage(exception = e)
        }
    }


    /**
     * Used to set up the checkbox UI for the survey
     *
     * @param type SurveyType - This is the type of survey question.
     * @param items List<String> - The list of options for the checkbox
     */
    private fun setUpCheckbox(type: SurveyType, items: List<String>) {
        try {
            binding.llCheckbox.removeAllViews()

            if (type != SurveyType.CHECKBOX || items.isEmpty()) return

            val answer = viewModel.uiState.value.selectedAnswer?.multipleChoiceAnswer

            items.forEachIndexed { index, option ->
                LayoutCheckboxItemBinding
                    .inflate(layoutInflater, null, false)
                    .apply {
                        // if the user already answered the question once
                        // we are selecting the one user has selected previously
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


    /**
     * Takes a list of strings and adds them to a radio group as radio buttons
     *
     * @param type SurveyType - This is the type of survey. It can be one of the following:
     * @param options List<String> - This is the list of options that the user can select from.
     */
    private fun setUpMultipleChoice(type: SurveyType, options: List<String>) {
        try {
            binding.rgMultipleChoice.removeAllViews()

            if (type != SurveyType.MULTIPLE_CHOICE || options.isEmpty()) return

            val answer = viewModel.uiState.value.selectedAnswer?.answerText

            // for each options we are creating a radio button
            // and adding it to the radio button group dynamically
            options.forEachIndexed { index, option ->
                // if the user already answered the question once
                // we are selecting the one user has answered previously
                val isThisChecked = option == answer
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


    /**
     * If the type is not drop down, or there are no options, we don't need to proceed further.
     * Otherwise, we set up the dropdown with the options, and if the user has already answered the
     * question, we select their answer
     *
     * @param type SurveyType - this is the type of the question, which can be either a drop down or a
     * text field
     * @param options List<String> - This is the list of options that the user can select from.
     */
    private fun setUpDropdown(type: SurveyType, options: List<String>) {
        // if there is not option, and the type is not drop down,
        // we don't need to proceed further
        if (type != SurveyType.DROP_DOWN || options.isEmpty()) return
        binding.spDropdown.adapter =
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                options
            )

        // we are checking the user has already answered it or not
        // if they have, we can select their answer previously given
        // or we won't select any particular one
        val answer = viewModel.uiState.value.selectedAnswer
        var selectedItem = -1
        options.forEachIndexed { index, option ->
            if (answer?.answerText == option) selectedItem = index
        }

        if (selectedItem >= 0) binding.spDropdown.setSelection(selectedItem)
    }


    private fun onNextButtonClick(view: View?) {
        view?.bounce()

        // we can load the next question if current answer is not required or if the answer is
        // already given
        val canLoadNextQuestion = !viewModel.isCurrentSurveyAnswerRequired()
                || submitAndCheckIfAnswerSubmitted()

        if (canLoadNextQuestion) {
            // we are checking if this is the last question or not
            // if it is, we are submitting the answer
            if (viewModel.hasSurveyReachedEnd()) viewModel.submitSurveyAnswers()
            // if it is not, we are loading next question
            else viewModel.loadNextSurvey()
        } else viewModel.makeUserMessage(messageText = "The answer is required.")

    }

    /**
     * Gets the answer of the current question, based on the type of the question
     *
     * @return Boolean, whether there is a answer of the question or not.
     */
    private fun submitAndCheckIfAnswerSubmitted(): Boolean {
        val selectedSurvey = viewModel.uiState.value.selectedSurvey

        // as different question have different type, and thus requires different collection technique
        // we are first checking its type, and using respective method.
        return when (selectedSurvey.type) {
            SurveyType.MULTIPLE_CHOICE -> getAnswerOfMultipleChoiceSurvey(selectedSurvey)
            SurveyType.NUMBER_INPUT -> getAnswerForNumberInputSurvey()
            SurveyType.TEXT_INPUT -> getAnswerOfTextInputSurvey()
            SurveyType.DROP_DOWN -> getAnswerOfDropDownSurvey()
            SurveyType.CHECKBOX -> getAnswerOfCheckboxes(selectedSurvey)
            SurveyType.CAMERA -> !viewModel.isCurrentSurveyAnswerRequired()
        }
    }

    /**
     * Gets the answer of the multiple choice survey question
     *
     * @return Boolean, whether there is a answer of the question or not.
     */
    private fun getAnswerOfMultipleChoiceSurvey(selectedSurvey: Survey): Boolean {
        return try {
            // gets the one option which have the index of the selected radio button
            val answer = selectedSurvey.options[binding.rgMultipleChoice.checkedRadioButtonId]
            viewModel.answerQuestion(answer)
            true
        } catch (e: Exception) {
            Timber.e(
                e,
                "Failed to get answer of multiple choice survey -> ${e.localizedMessage}"
            )
            viewModel.makeUserMessage(exception = e)
            false
        }
    }

    /**
     * Gets the answer of the number input survey question
     *
     * @return Boolean, whether there is a answer of the question or not.
     */
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

    /**
     * Gets the answer of the text input survey question
     *
     * @return Boolean, whether there is a answer of the question or not.
     */
    private fun getAnswerOfTextInputSurvey(): Boolean {
        return try {
            val answer = binding.tietInput.text.toString()
            if (answer.isBlank()) return false
            viewModel.answerQuestion(answer)
            true
        } catch (e: Exception) {
            viewModel.makeUserMessage(exception = e)
            false
        }
    }


    /**
     * Gets the answer of the drop down survey
     *
     * @return Boolean, whether there is a answer of the question or not.
     */
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

    /**
     * Gets the answer of checkbox type questions
     *
     * @param selectedSurvey Survey
     * @return Boolean, whether there is a answer of the question or not.
     */
    private fun getAnswerOfCheckboxes(selectedSurvey: Survey): Boolean {
        try {
            val answers = mutableListOf<String>()

            // iterates over all the children of linear layout,
            // which are custom layout we have bound before
            // to options of the multiple choice questions
            binding.llCheckbox.children.forEachIndexed { index, view ->
                if (view is MaterialCheckBox && view.isChecked)
                // we are adding option to answer, if the option's checkbox is checked
                    answers.add(selectedSurvey.options[index])
            }

            if (answers.isEmpty()) return false
            viewModel.answerQuestion(answers)

            return true
        } catch (e: Exception) {
            viewModel.makeUserMessage(exception = e)
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