package io.github.kabirnayeem99.v2_survey.presentation.previousAnswers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import io.github.kabirnayeem99.v2_survey.databinding.LayoutItemAnswerClusterBinding
import io.github.kabirnayeem99.v2_survey.domain.entity.AnsweredSurveyCluster
import timber.log.Timber

class AnswerClusterAdapter : RecyclerView.Adapter<AnswerClusterAdapter.AnswerClusterHolder>() {


    private val viewPool = RecycledViewPool()

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
        Timber.d(answerCluster.toString())
        val binding = holder.binding
        binding.apply {
            tvTime.text = answerCluster.time.toString()
            val eachAnswerAdapter = EachAnswerAdapter()
            eachAnswerAdapter.submitAnswerList(answerCluster.answeredSurveyList)
            Timber.d(answerCluster.answeredSurveyList.toString())
            binding.rvAnswersList.apply {
                adapter = eachAnswerAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    override fun getItemCount() = differ.currentList.size
}