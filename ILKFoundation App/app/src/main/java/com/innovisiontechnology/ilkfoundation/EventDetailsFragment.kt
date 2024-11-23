package com.innovisiontechnology.ilkfoundation

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class EventDetailsFragment : DialogFragment() {

    //Variables
    private lateinit var ivEvent: ImageView
    private lateinit var tvEventName: TextView
    private lateinit var tvEventDateTime: TextView
    private lateinit var tvEventLocation: TextView
    private lateinit var tvEventDesc: TextView
    private lateinit var btnClose: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_event_details, container, false)

        //Typecast the variables
        ivEvent = view.findViewById(R.id.ivEvent)
        tvEventName = view.findViewById(R.id.tvEventName)
        tvEventDateTime = view.findViewById(R.id.tvEventDateTime)
        tvEventLocation = view.findViewById(R.id.tvEventLocation)
        tvEventDesc = view.findViewById(R.id.tvEventDesc)
        btnClose = view.findViewById(R.id.btnClose)

        //Function call to display the event details
        populateTheFragment()

        //btnClick Events
        btnClose.setOnClickListener {
            //Close the fragment
            dismiss()
        }

        return view
    }

    //Function to display the event details
    private fun populateTheFragment()
    {
        //Get the event details from arguments
        val eventName = arguments?.getString("eventName")
        val eventDate = arguments?.getString("eventDate")
        val eventTime = arguments?.getString("eventTime")
        val eventLocation = arguments?.getString("eventLocation")
        val eventDescription = arguments?.getString("eventDesc")
        Log.d("EventDetailsFragment", "Event Description from Bundle: $eventDescription")

        //Update the .xml with the values
        tvEventName.text = eventName
        tvEventDateTime.text = "$eventDate at $eventTime"
        tvEventLocation.text = eventLocation
        tvEventDesc.text = eventDescription
    }

}