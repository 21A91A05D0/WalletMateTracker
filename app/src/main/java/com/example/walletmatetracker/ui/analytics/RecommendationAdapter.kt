package com.example.walletmatetracker.ui.analytics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.walletmatetracker.databinding.ItemRecommendationBinding
import com.example.walletmatetracker.domain.model.Recommendation

class RecommendationAdapter :
    ListAdapter<Recommendation, RecommendationAdapter.ViewHolder>(
        DiffCallback()
    ) {

    class ViewHolder(val binding: ItemRecommendationBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : ViewHolder {

        val binding = ItemRecommendationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.binding.tvTitle.text = item.title
        holder.binding.tvDescription.text = item.description
    }

    class DiffCallback :
        DiffUtil.ItemCallback<Recommendation>() {

        override fun areItemsTheSame(
            oldItem: Recommendation,
            newItem: Recommendation
        ) = oldItem.title == newItem.title

        override fun areContentsTheSame(
            oldItem: Recommendation,
            newItem: Recommendation
        ) = oldItem == newItem
    }
}
