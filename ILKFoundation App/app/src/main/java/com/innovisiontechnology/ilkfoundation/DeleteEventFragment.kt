package com.innovisiontechnology.ilkfoundation

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.firestore.FirebaseFirestore


class DeleteEventFragment : DialogFragment() {

    //Variables
    private lateinit var btnBack: Button
    private lateinit var btnDelete: Button
    private var onEventDeletedListener: ((String) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_delete_event, container, false)

        //Typecast the variables
        btnBack = view.findViewById(R.id.btnBack)
        btnDelete = view.findViewById(R.id.btnDelete)

        //Btn click events
        btnBack.setOnClickListener {
            //Close the fragment
            dismiss()
        }

        btnDelete.setOnClickListener {
            //Function call to delete the selected event
            deleteEvent()
        }

        //Get the event details
        val eventName = arguments?.getString("eventName")
        val eventDate = arguments?.getString("eventDate")
        val eventTime = arguments?.getString("eventTime")
        val eventImageUrl = arguments?.getString("eventImageUrl")

        //Bind the data to the views in the layout
        val tvEventName: TextView = view.findViewById(R.id.tvEventName)
        val tvEventDateTime: TextView = view.findViewById(R.id.tvEventDateTime)
        val ivEvent: ImageView = view.findViewById(R.id.ivEvent)

        tvEventName.text = eventName
        tvEventDateTime.text = "$eventDate at $eventTime"

        Glide.with(requireContext())
            .load(eventImageUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .error(R.drawable.default_image)
            .into(ivEvent)

        return view
    }

    //Function to delete the event
    private fun deleteEvent()
    {
        //Get the eventId from the arguments
        val eventId = arguments?.getString("eventId") ?: return

        //Firestore reference
        val firestore = FirebaseFirestore.getInstance()

        //Get the document reference using the eventId
        val eventRef = firestore.collection("events").document(eventId)

        //Delete the event document
        eventRef.delete()
            .addOnSuccessListener {

                //Notify the listener after deletion
                onEventDeletedListener?.invoke(eventId)

                //Notify the user that the event was deleted successfully
                context?.let {
                    Toast.makeText(it, "Event deleted successfully", Toast.LENGTH_SHORT).show()
                }
                //Close the delete event fragment
                dismiss()
            }
            .addOnFailureListener { e ->
                //Notify the user if there was an error deleting the event
                e.printStackTrace()

                //Notify the user when an error occurs
                context?.let {
                    Toast.makeText(it, "Failed to delete event: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun setOnEventDeletedListener(listener: (String) -> Unit) {
        onEventDeletedListener = listener
    }

}