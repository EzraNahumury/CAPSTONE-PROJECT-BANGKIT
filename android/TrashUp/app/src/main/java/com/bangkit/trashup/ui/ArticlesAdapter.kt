package com.bangkit.trashup.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.trashup.data.remote.response.DatasItem
import com.bumptech.glide.Glide
import com.bangkit.trashup.databinding.ItemRowArticlesBinding

class ArticlesAdapter(private val onItemClickCallback: OnItemClickCallback) : ListAdapter<DatasItem, ArticlesAdapter.ArticlesViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticlesViewHolder {
        val binding = ItemRowArticlesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticlesViewHolder(binding, onItemClickCallback)
    }

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        val articles = getItem(position)
        if (articles != null) {
            holder.bind(articles)
            holder.itemView.setOnClickListener {
                onItemClickCallback.onItemClick(articles)
            }
        }
    }

    class ArticlesViewHolder(private val binding: ItemRowArticlesBinding, private val listener: OnItemClickCallback) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tutorials: DatasItem) {
            val context = binding.root.context

            binding.tvItemName.text = tutorials.title

            Glide.with(context)
                .load(tutorials.pitcURL)
                .into(binding.ivItemPhoto)

            binding.root.setOnClickListener {
                listener.onItemClick(tutorials)
            }
        }
    }

    interface OnItemClickCallback {
        fun onItemClick(articles: DatasItem)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DatasItem>() {
            override fun areItemsTheSame(oldItem: DatasItem, newItem: DatasItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DatasItem, newItem: DatasItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}
