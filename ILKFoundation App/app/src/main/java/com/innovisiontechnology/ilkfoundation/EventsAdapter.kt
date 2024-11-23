import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.innovisiontechnology.ilkfoundation.Event
import com.innovisiontechnology.ilkfoundation.EventDetailsFragment
import com.innovisiontechnology.ilkfoundation.R

class EventsAdapter(private val eventsList: List<Event>, private val activity: FragmentActivity,) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.event_card, parent, false)
        return EventViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventsList[position]
        holder.bind(event)
    }

    override fun getItemCount(): Int = eventsList.size

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Declare the variables and initialise them
        private val eventImage: ImageView = itemView.findViewById(R.id.eventImage)
        private val eventName: TextView = itemView.findViewById(R.id.eventName)
        private val eventDateTime: TextView = itemView.findViewById(R.id.eventDateTime)
        private val eventLocation: TextView = itemView.findViewById(R.id.eventLocation)
        private val btnViewEvent: Button = itemView.findViewById(R.id.btnViewEvent)

        fun bind(event: Event) {
            eventName.text = event.name
            eventDateTime.text = "${event.date} at ${event.time}"
            eventLocation.text = event.location

            //Load the image
            Glide.with(itemView.context)
                .load(event.imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                //Set a default image if no image URL
                .error(R.drawable.burger)
                .into(eventImage)

            //Button click
            btnViewEvent.setOnClickListener {
                Log.d("EventsAdapter", "Event Description: ${event.description}")

                val dialogFragment = EventDetailsFragment()

                // Create a bundle to pass event details
                val bundle = Bundle().apply {
                    putString("eventName", event.name)
                    putString("eventDate", event.date)
                    putString("eventTime", event.time)
                    putString("eventLocation", event.location)
                    putString("eventDesc", event.description)
                    putString("eventImageUrl", event.imageUrl)
                }

                dialogFragment.arguments = bundle

                // Show the dialog
                dialogFragment.show(activity.supportFragmentManager, "EventDetailsDialog")
            }
        }
    }
}


