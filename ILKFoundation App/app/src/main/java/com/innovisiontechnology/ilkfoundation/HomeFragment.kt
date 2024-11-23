package com.innovisiontechnology.ilkfoundation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Get the TextView
        val textView = view.findViewById<TextView>(R.id.missionText1)
        val fullText = "OUR CAUSES"
        val spannableString = SpannableString(fullText)

        // Define color resources
        val colors = listOf(
            R.color.sushi,
            R.color.old_copper,
            R.color.yellow

        )

        // Inside onCreateView in HomeFragment
        val bottomImage = view.findViewById<ImageView>(R.id.bottomImage)
        bottomImage.setOnClickListener {
            // URL with address for Google Maps
            val address = "56+Mfuphi+Threeway+Ridgview+Durban+South+Africa"
            val mapIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/search/?api=1&query=$address")
            )
            startActivity(mapIntent)
        }

        //Apply color from colors.xml every 2 letters
        for (i in 4 until fullText.length) { // Only coloring "CAUSES"
            if (fullText[i] != ' ') {
                val colorIndex = ((i - 4) / 2) % colors.size // Adjust index for "CAUSES"
                val color = ContextCompat.getColor(requireContext(), colors[colorIndex])
                spannableString.setSpan(
                    ForegroundColorSpan(color),
                    i,
                    i + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        //Set the spannable string to the TextView
        textView.text = spannableString

        //setAdminForCurrentUser(requireContext())

        return view
    }

    //Set the admin
    fun setAdminForCurrentUser(context: Context)
    {
        // Get the current user's UID
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null)
        {
            val uid = currentUser.uid

            // Get a reference to the Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users").child(uid)

            // Create a map to store the value
            val adminData = mapOf("isAdmin" to true)

            // Save the value to the database
            userRef.setValue(adminData)
                .addOnSuccessListener {
                    // Successfully written to the database
                    Toast.makeText(context, "Admin status set successfully for user: $uid", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    // Handle any errors
                    Toast.makeText(context, "Failed to set admin status: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "No authenticated user found.", Toast.LENGTH_SHORT).show()
        }
    }

}