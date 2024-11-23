package com.innovisiontechnology.ilkfoundation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class DeleteEventAdapter (private val eventsList: List<Event>, private val onEventClick: (Event, String) -> Unit) : RecyclerView.Adapter<DeleteEventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.delete_event_item, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventsList[position]
        holder.bind(event)

        //Click listener for the event
        holder.itemView.setOnClickListener {
            //Trigger the callback
            onEventClick(event, event.id)
        }
    }

    override fun getItemCount(): Int = eventsList.size

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val eventImage: ImageView = itemView.findViewById(R.id.ivEvent)
        private val eventName: TextView = itemView.findViewById(R.id.tvEventName)
        private val eventDateTime: TextView = itemView.findViewById(R.id.tvEventDateTime)

        fun bind(event: Event) {
            eventName.text = event.name
            eventDateTime.text = "${event.date} at ${event.time}"

            Glide.with(itemView.context)
                .load(event.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //Set a default image if no image URL
                .error(R.drawable.default_image)
                .into(eventImage)
        }
    }
}