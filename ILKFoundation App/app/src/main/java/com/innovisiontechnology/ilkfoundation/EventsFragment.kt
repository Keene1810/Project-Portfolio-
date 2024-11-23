package com.innovisiontechnology.ilkfoundation

import EventsAdapter
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.usage.UsageEvents
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class EventsFragment : Fragment() {

    // Variables

    //Past events
    private lateinit var rvPastEvents: RecyclerView
    private lateinit var pastEventsAdapter: EventsAdapter
    private lateinit var pastEventsList: MutableList<Event>
    //Upcoming events
    private lateinit var rvUpcomingEvents: RecyclerView
    private lateinit var upcomingEventsAdapter: EventsAdapter
    private lateinit var upcomingEventsList: MutableList<Event>
    //TextViews in case the recycler views are empty
    private lateinit var ivNoPastEvents: ImageView
    private lateinit var tvNoPastEvents: TextView
    private lateinit var ivNoUpcomingEvents: ImageView
    private lateinit var tvNoUpcomingEvents: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_events, container, false)

        //Typecast the variables
        rvPastEvents = view.findViewById(R.id.rvPastEvents)
        rvUpcomingEvents = view.findViewById(R.id.rvUpcomingEvents)
        ivNoPastEvents = view.findViewById(R.id.ivNoPastEvents)
        tvNoPastEvents = view.findViewById(R.id.tvNoPastEvents)
        ivNoUpcomingEvents = view.findViewById(R.id.ivNoUpcomingEvents)
        tvNoUpcomingEvents = view.findViewById(R.id.tvNoUpcomingEvents)

        //Initialize the lists
        pastEventsList = mutableListOf()
        upcomingEventsList = mutableListOf()

        //Initialize the adapters
        pastEventsAdapter = EventsAdapter(pastEventsList, requireActivity())
        upcomingEventsAdapter = EventsAdapter(upcomingEventsList, requireActivity())

        //Set up the layout manager and adapters for both RecyclerViews
        rvPastEvents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rvUpcomingEvents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        //Set the adapters for the RecyclerViews
        rvPastEvents.adapter = pastEventsAdapter
        rvUpcomingEvents.adapter = upcomingEventsAdapter

        //Function to fetch events to display
        fetchEventsFromFirestore()

        return view
    }

    //Function that fetches the events from Firestore
    private fun fetchEventsFromFirestore()
    {
        //Create a ProgressDialog
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading Events...")
        progressDialog.setCancelable(true)
        //Show the ProgressDialog
        progressDialog.show()

        //Firestore reference
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("events")
            .get()
            .addOnSuccessListener { result ->
                //Clear the lists before adding new events
                pastEventsList.clear()
                upcomingEventsList.clear()

                //Get the current date in the format used in Firestore
                val currentDate = Calendar.getInstance().time
                val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())

                //Loop through all documents and categorize them into past and upcoming events
                for (document in result)
                {
                    val eventId = document.id
                    val eventName = document.getString("name") ?: ""
                    val eventLocation = document.getString("location") ?: ""
                    val eventDateString = document.getString("date") ?: ""
                    val eventTime = document.getString("time") ?: ""
                    val imageUrl = document.getString("imageUri") ?: ""
                    val eventDesc = document.getString("description") ?: ""

                    try {
                        //Parse eventDateString to Date
                        val eventDate = dateFormat.parse(eventDateString)

                        //Compare parsed date with the current date
                        if (eventDate != null && eventDate.before(currentDate))
                        {
                            //If the event date is before today, it goes to the past events list
                            pastEventsList.add(Event(eventId, eventName, eventDateString, eventTime, eventLocation, eventDesc, imageUrl))

                            Log.d("EventsAdapter", "Event Description: $eventDesc")
                        } else {
                            //If the event date is today or in the future, it goes to the upcoming events list
                            upcomingEventsList.add(Event(eventId, eventName, eventDateString, eventTime, eventLocation, eventDesc, imageUrl))
                        }
                    } catch (e: Exception) {
                        //Log any parsing errors
                        e.printStackTrace()
                    }
                }

                //Notify adapters to refresh the data
                pastEventsAdapter.notifyDataSetChanged()
                upcomingEventsAdapter.notifyDataSetChanged()

                //Show "No events available" if the lists are empty
                ivNoPastEvents.visibility = if (pastEventsList.isEmpty()) View.VISIBLE else View.GONE
                tvNoPastEvents.visibility = if (pastEventsList.isEmpty()) View.VISIBLE else View.GONE
                ivNoUpcomingEvents.visibility = if (upcomingEventsList.isEmpty()) View.VISIBLE else View.GONE
                tvNoUpcomingEvents.visibility = if (upcomingEventsList.isEmpty()) View.VISIBLE else View.GONE

                //Close the ProgressDialog after data has loaded
                progressDialog.dismiss()
            }
            .addOnFailureListener { e ->
                e.printStackTrace()

                //Close the ProgressDialog
                progressDialog.dismiss()
            }
    }


}