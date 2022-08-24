package io.github.kabirnayeem99.v2_survey.presentation.previousAnswers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.kabirnayeem99.v2_survey.databinding.LayoutItemEachAnswerBinding
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurvey
import timber.log.Timber

class EachAnswerAdapter : RecyclerView.Adapter<EachAnswerAdapter.EachAnswerHolder>() {

    inner class EachAnswerHolder(val binding: LayoutItemEachAnswerBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<AnsweredSurvey>() {
        override fun areItemsTheSame(oi: AnsweredSurvey, ni: AnsweredSurvey) =
            oi.id == ni.id

        override fun areContentsTheSame(oi: AnsweredSurvey, ni: AnsweredSurvey) =
            oi == ni
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitAnswerList(answersParam: List<AnsweredSurvey>) {
        Timber.d(answersParam.toString())
        differ.submitList(answersParam)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EachAnswerHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemEachAnswerBinding.inflate(layoutInflater, parent, false)
        return EachAnswerHolder(binding)
    }

    override fun onBindViewHolder(holder: EachAnswerHolder, position: Int) {
        val answer = differ.currentList[position]
        Timber.d(answer.toString())
        holder.binding.apply {
            tvAnswer.text = answer.answerText ?: ""
            tvQuestion.text = answer.question
        }
    }

    override fun getItemCount() = differ.currentList.size


}
