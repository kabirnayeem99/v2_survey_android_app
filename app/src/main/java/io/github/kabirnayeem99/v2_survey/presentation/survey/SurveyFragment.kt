package io.github.kabirnayeem99.v2_survey.presentation.survey

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import io.github.kabirnayeem99.v2_survey.databinding.FragmentSurveyBinding
import io.github.kabirnayeem99.v2_survey.databinding.LayoutCheckboxItemBinding
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

    /**
     * Takes a `SurveyUiState` object as a parameter and updates the UI based on the state of the
     * object
     *
     * @param uiState SurveyUiState - This is the data class that holds all the data that is required
     * to render the UI.
     */
    private fun handleUiState(uiState: SurveyUiState) {
        uiState.apply {
            if (isLoading) loading.show() else loading.dismiss()
            binding.apply {

                lpiSurveyProgress.progress = progress
                tvQuestion.text = selectedSurvey.question
                tvQuestionNo.text = "Q. ${currentSurveyIndex + 1}"

                manageVisibility(uiState.selectedSurvey.type)

                setUpDropdown(selectedSurvey.type, selectedSurvey.options)
                setUpCheckbox(selectedSurvey.type, selectedSurvey.options)
                setUpMultipleChoice(selectedSurvey.type, selectedSurvey.options)
                setUpCamera(selectedSurvey.type)

                if (isSurveyAtEnd) btnNext.text = getString(R.string.label_finish)
                if (!isSurveyAtEnd) btnNext.text = getString(R.string.label_next)
            }
        }
    }

    /**
     * Manages the visibility of the different views in the layout.
     *
     * @param type The type of survey question.
     */
    private fun manageVisibility(type: SurveyType) {
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
            if (uri != null) {
                try {
                    binding.ivCamera.setImageURI(uri)
                } catch (e: Exception) {
                    Timber.e(e, "Failed to set image uri -> ${e.localizedMessage}")
                }
            } else {
                Timber.e("Failed to take image")
            }
        }


    private fun setUpCamera(type: SurveyType) {
        if (type != SurveyType.CAMERA) return
        val hasCameraPermission = EasyPermissions
            .hasPermissions(requireContext(), android.Manifest.permission.CAMERA)
        if (!hasCameraPermission) {
            (activity as ContainerActivity).askForCameraPermission()
            return
        }
        binding.ivCamera.setOnClickListener { startForResult.launch("image/*") }
    }

    /**
     * Inflates a layout file, sets the text of a checkbox, and adds the checkbox to a
     * LinearLayout
     *
     * @param type SurveyType - This is the type of survey. It can be either a radio button, checkbox,
     * or text input.
     * @param items List<String> - This is the list of options that will be displayed in the checkbox.
     * @return A list of strings.
     */
    private fun setUpCheckbox(type: SurveyType, items: List<String>) {
        binding.llCheckbox.removeAllViews()

        if (type != SurveyType.CHECKBOX || items.isEmpty()) return
        try {
            items.forEachIndexed { index, option ->
                LayoutCheckboxItemBinding
                    .inflate(layoutInflater, null, false)
                    .apply {
                        mcbCheckbox.text = option
                        mcbCheckbox.id = index
                        mcbCheckbox.setOnCheckedChangeListener { btn, isChecked ->
                            Timber.d(btn.text.toString())
                        }
                        binding.llCheckbox.addView(root)
                    }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to add options -> ${e.localizedMessage}.")
        }
    }


    /**
     * Takes in a list of strings and adds them to a radio group as radio buttons
     *
     * @param type SurveyType - This is the type of survey. It can be one of the following:
     * @param items List<String>
     * @return A list of strings.
     */
    private fun setUpMultipleChoice(type: SurveyType, items: List<String>) {
        binding.rgMultipleChoice.removeAllViews()

        if (type != SurveyType.MULTIPLE_CHOICE || items.isEmpty()) return

        try {
            items.forEachIndexed { index, option ->
                val btn = RadioButton(requireContext())
                btn.id = index
                btn.text = option
                binding.rgMultipleChoice.addView(btn)
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to add options -> ${e.localizedMessage}.")
        }
    }

    /**
     * If the survey type is a dropdown and the items list is not empty, set the adapter of the spinner
     * to an ArrayAdapter with the items list
     *
     * @param type The type of the survey question.
     * @param items List<String> - The list of items to be displayed in the dropdown
     * @return The binding object is being returned.
     */
    private fun setUpDropdown(type: SurveyType, items: List<String>) {
        if (type != SurveyType.DROP_DOWN || items.isEmpty()) return
        binding.spDropdown.adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, items)
    }

    /**
     * When the next button is clicked, the button will bounce, and if the survey is not at the end,
     * the next survey will be loaded
     *
     * @param view View? - The view that was clicked.
     */
    private fun onNextButtonClick(view: View?) {
        view?.bounce()
        if (!viewModel.isCurrentSurveyAnswerRequired() || getAnswer()) {
            if (!viewModel.uiState.value.isSurveyAtEnd) viewModel.loadNextSurvey()
            else Toast.makeText(context, "You are finished.", Toast.LENGTH_LONG).show()
            return
        }
        Toast.makeText(context, "The answer is required.", Toast.LENGTH_LONG).show()
    }

    private fun getAnswer(): Boolean {
        val selectedSurvey = viewModel.uiState.value.selectedSurvey

        if (selectedSurvey.type == SurveyType.MULTIPLE_CHOICE) {
            val answer = selectedSurvey.options[binding.rgMultipleChoice.checkedRadioButtonId]
            viewModel.answerQuestion(answer)
            return true
        }

        if (selectedSurvey.type == SurveyType.NUMBER_INPUT) {
            val answer = binding.tietNumInput.text.toString()
            if (answer.isBlank()) return false
            viewModel.answerQuestion(answer)
            return true
        }

        if (selectedSurvey.type == SurveyType.TEXT_INPUT) {
            val answer = binding.tietInput.text.toString()
            if (answer.isBlank()) return false
            viewModel.answerQuestion(answer)
            return true
        }

        if (selectedSurvey.type == SurveyType.DROP_DOWN) {
            val dropDownAnswer = binding.spDropdown.selectedItem.toString()
            if (dropDownAnswer.isEmpty()) return false
            viewModel.answerQuestion(dropDownAnswer)
            return true
        }

        if (selectedSurvey.type == SurveyType.CHECKBOX) {
            val answers = mutableListOf<String>()

            binding.llCheckbox.children.forEachIndexed { index, view ->
                if (view is MaterialCheckBox) {
                    if (view.isChecked) answers.add(selectedSurvey.options[index])
                }
            }

            if (answers.isEmpty()) return false
            viewModel.answerQuestion(answers)

            return true
        }

        if (selectedSurvey.type == SurveyType.CAMERA) {

        }

        return false
    }


    /**
     * When the previous button is clicked, the view is bounced and the previous survey is loaded
     *
     * @param view View? - The view that was clicked
     */
    private fun onPreviousButtonClick(view: View?) {
        view?.bounce()
        viewModel.loadPrevSurvey()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}