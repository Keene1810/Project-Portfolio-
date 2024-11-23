package com.insight24app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.insight24app.databinding.ItemNewsBinding
import com.insight24app.model.Article

class NewsAdapter(
    private val itemList: List<Article>,
    private val onItemSelected: (Article) -> Unit,
    private val onRemoveSelected: (Article) -> Unit // Lambda for remove action
) : RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemNewsBinding.inflate(inflater, parent, false)
        return ViewHolder(binding, onItemSelected, onRemoveSelected)
    }

    override fun getItemCount(): Int = itemList.size

    class ViewHolder(
        val binding: ItemNewsBinding,
        private val onItemSelected: (Article) -> Unit,
        private val onRemoveSelected: (Article) -> Unit // Capture the remove action
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Article) {
            binding.title.text = item.title
            binding.source.text = item.source.name
            binding.date.text = item.publishedAt
            binding.description.text = item.description
            Glide.with(binding.root).load(item.urlToImage).into(binding.image)

            binding.root.setOnClickListener {
                onItemSelected.invoke(item)
            }

            // Set up the remove button
            binding.removeButton.isVisible
            binding.removeButton.setOnClickListener { // Assuming you have a remove button in your layout
                onRemoveSelected.invoke(item)
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
    }
}
