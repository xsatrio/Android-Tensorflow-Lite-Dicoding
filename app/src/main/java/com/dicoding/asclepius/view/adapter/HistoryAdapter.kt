package com.dicoding.asclepius.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ItemHistoryBinding

class HistoryAdapter(private val onDeleteClick: (HistoryEntity) -> Unit) :
    ListAdapter<HistoryEntity, HistoryAdapter.EventViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding, onDeleteClick)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val history = getItem(position)
        holder.bind(history)
    }

    class EventViewHolder(
        private val binding: ItemHistoryBinding,
        private val onDeleteClick: (HistoryEntity) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryEntity) {
            binding.tvDate.text = history.date
            "Hasil : ${history.analysis}".also { binding.tvPredict.text = it }
            "Waktu Inferensi : ${history.inference}ms".also { binding.tvInference.text = it }
            Glide.with(itemView.context)
                .load(history.image)
                .apply(
                    RequestOptions.placeholderOf(R.drawable.ic_loading)
                        .error(R.drawable.ic_error)
                )
                .into(binding.ivMediaCover)

            binding.btnDelete.setOnClickListener {
                onDeleteClick(history)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryEntity>() {
            override fun areItemsTheSame(
                oldItem: HistoryEntity,
                newItem: HistoryEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: HistoryEntity,
                newItem: HistoryEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}