package com.innovisiontechnology.ilkfoundation

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class SelectEventToDeleteFragment : DialogFragment() {

    //Variables
    private lateinit var rvDeleteEvents: RecyclerView
    private lateinit var deleteEventAdapter: DeleteEventAdapter
    private lateinit var eventsList: MutableList<Event>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_select_event_to_delete, container, false)

        //Typecast the variables
        rvDeleteEvents = view.findViewById(R.id.rvDeleteEvents)

        //Initialize the list
        eventsList = mutableListOf()

        //Initialize the adapters
        deleteEventAdapter = DeleteEventAdapter(eventsList) { event, eventId ->

            //Handle navigation to another fragment with event details
            val deleteEventFragment = DeleteEventFragment().apply {
                arguments = Bundle().apply {
                    putString("eventId", eventId)
                    putString("eventName", event.name)
                    putString("eventDate", event.date)
                    putString("eventTime", event.time)
                    putString("eventImageUrl", event.imageUrl)
                }

                //Callback to remove the deleted event from the list
                setOnEventDeletedListener { deletedEventId ->
                    eventsList.removeAll { it.id == deletedEventId }
                    deleteEventAdapter.notifyDataSetChanged()
                }
            }

            //Show the dialog fragment
            deleteEventFragment.show(parentFragmentManager, "FragmentDeleteEvent")
        }

        //Set up the layout manager and adapters for both RecyclerViews
        rvDeleteEvents.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        //Set the adapter for the RecyclerViews
        rvDeleteEvents.adapter = deleteEventAdapter

        //Function to fetch events to display
        fetchEventsFromFirestore()

        return view
    }

    private fun fetchEventsFromFirestore()
    {
        //Create a ProgressDialog
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading Events...")
        progressDialog.setCancelable(false)
        //Show the ProgressDialog
        progressDialog.show()

        //Firestore reference
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("events")
            .get()
            .addOnSuccessListener { result ->
                //Clear the list before adding new events
                eventsList.clear()

                //Loop through all documents and categorize them into past and upcoming events
                for (document in result)
                {
                    val eventId = document.id
                    val imageUrl = document.getString("imageUri") ?: ""
                    val eventName = document.getString("name") ?: ""
                    val eventDateString = document.getString("date") ?: ""
                    val eventTime = document.getString("time") ?: ""
                    val eventDesc = ""
                    val eventLocation = ""

                    eventsList.add(Event(eventId, eventName, eventDateString, eventTime, eventLocation, eventDesc, imageUrl))

                    //Pass the eventId to the adapter to handle item clicks
                    deleteEventAdapter.notifyDataSetChanged()
                }

                //Notify adapter to refresh the data
                deleteEventAdapter.notifyDataSetChanged()

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