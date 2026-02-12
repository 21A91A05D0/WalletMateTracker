package com.example.walletmatetracker.ui.analytics

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.walletmatetracker.databinding.ItemRecommendationBinding
import com.example.walletmatetracker.domain.model.Recommendation
import com.example.walletmatetracker.domain.model.RecommendationType

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

//        when (item.type) {
//
//            RecommendationType.HEALTH_SCORE -> {
//
//                holder.binding.root.setCardBackgroundColor(
//                    when {
//                        item.title.contains("Good") ->
//                            Color.parseColor("#E8F5E9")
//
//                        item.title.contains("Moderate") ->
//                            Color.parseColor("#FFF8E1")
//
//                        else ->
//                            Color.parseColor("#FFEBEE")
//                    }
//                )
//            }
//
//            else -> {
//                holder.binding.root.setCardBackgroundColor(Color.WHITE)
//            }
//        }
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
