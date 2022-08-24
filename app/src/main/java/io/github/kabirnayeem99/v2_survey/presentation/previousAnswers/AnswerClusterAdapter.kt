package io.github.kabirnayeem99.v2_survey.presentation.previousAnswers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import io.github.kabirnayeem99.v2_survey.databinding.LayoutItemAnswerClusterBinding
import io.github.kabirnayeem99.v2_survey.databinding.LayoutItemEachAnswerBinding
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurveyCluster

class AnswerClusterAdapter : RecyclerView.Adapter<AnswerClusterAdapter.AnswerClusterHolder>() {

    lateinit var layoutInflater: LayoutInflater

    private val diffCallback = object : DiffUtil.ItemCallback<AnsweredSurveyCluster>() {
        override fun areItemsTheSame(oi: AnsweredSurveyCluster, ni: AnsweredSurveyCluster) =
            oi.id == ni.id

        override fun areContentsTheSame(oi: AnsweredSurveyCluster, ni: AnsweredSurveyCluster) =
            oi == ni
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitAnswerClusterList(clustersParam: List<AnsweredSurveyCluster>) {
        differ.submitList(clustersParam)
    }

    inner class AnswerClusterHolder(val binding: LayoutItemAnswerClusterBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerClusterHolder {
        layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemAnswerClusterBinding.inflate(layoutInflater, parent, false)
        return AnswerClusterHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerClusterHolder, position: Int) {
        val answerCluster = differ.currentList[position]
        val binding = holder.binding
        binding.apply {
            tvTime.text = answerCluster.time.toString()
            answerCluster.answeredSurveyList.forEach { eachAnswer ->
                LayoutItemEachAnswerBinding.inflate(layoutInflater, null, false)
                    .also { eachAnswerBinding ->
                        eachAnswerBinding.tvQuestion.text = eachAnswer.question
                        eachAnswerBinding.tvAnswer.text = eachAnswer.answerText ?: ""
                        llAnswersList.addView(eachAnswerBinding.root)
                    }
            }
        }
    }

    override fun getItemCount() = differ.currentList.size
}