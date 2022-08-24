package io.github.kabirnayeem99.v2_survey.presentation.previousAnswers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.kabirnayeem99.v2_survey.core.utility.toFormattedDate
import io.github.kabirnayeem99.v2_survey.databinding.LayoutItemAnswerClusterBinding
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurveyCluster

class AnswerClusterAdapter : RecyclerView.Adapter<AnswerClusterAdapter.AnswerClusterHolder>() {

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
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemAnswerClusterBinding.inflate(layoutInflater, parent, false)
        return AnswerClusterHolder(binding)
    }

    override fun onBindViewHolder(holder: AnswerClusterHolder, position: Int) {
        val answerCluster = differ.currentList[position]
        val binding = holder.binding
        binding.apply {
            tvTime.text = answerCluster.time.toFormattedDate()
            // we are using nested recycler view, as the question number can be any
            val eachAnswerAdapter = EachAnswerAdapter()
            eachAnswerAdapter.submitAnswerList(answerCluster.answeredSurveyList)
            binding.rvAnswersList.apply {
                adapter = eachAnswerAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun getItemCount() = differ.currentList.size
}