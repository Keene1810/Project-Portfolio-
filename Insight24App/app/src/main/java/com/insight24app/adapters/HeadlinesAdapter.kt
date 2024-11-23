package com.insight24app.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.insight24app.R
import com.insight24app.model.InsightHeadlines


class HeadlinesAdapter : RecyclerView.Adapter<HeadlinesAdapter.HeadlineViewHolder>() {

    private var headlines: List<InsightHeadlines> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_headline, parent, false)
        return HeadlineViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeadlineViewHolder, position: Int) {
        val headline = headlines[position]
        holder.bind(headline)
    }

    override fun getItemCount(): Int = headlines.size

    fun submitList(newHeadlines: List<InsightHeadlines>) {
        headlines = newHeadlines
        notifyDataSetChanged()
    }

    class HeadlineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val date:TextView = itemView.findViewById(R.id.date)

        fun bind(headline: InsightHeadlines) {
            titleTextView.text = headline.title
            descriptionTextView.text = headline.description
            date.text = headline.date

        }
    }
}
