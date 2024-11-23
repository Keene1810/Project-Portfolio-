package com.innovisiontechnology.ilkfoundation

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MoreFragment : Fragment() {

    //Variables
    private lateinit var flAboutUs: FrameLayout
    private lateinit var flLogout: FrameLayout
    private lateinit var flAdmin: FrameLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        //Typecast the variables
        flAboutUs = view.findViewById(R.id.flAboutUs)
        flLogout = view.findViewById(R.id.flLogout)
        flAdmin = view.findViewById(R.id.flAdmin)

        //onClickListeners
        flAboutUs.setOnClickListener{
            //Go to the About Us fragment
            val intent = Intent(requireContext(), AboutUs::class.java)
            startActivity(intent)
        }

        flLogout.setOnClickListener {
            //Function call to log the user out
            logoutTheUser()
        }

        flAdmin.setOnClickListener {
            //Move to the Admin Features screen
            val intent = Intent(requireContext(),AdminFeatures::class.java)
            startActivity(intent)
        }

        //Check if the user is an admin and decide whether to show admin options
        checkAdminStatusAndShowLayout(requireContext(), flAdmin)

        return view
    }

    //Function that logs the user out
    private fun logoutTheUser()
    {
        //Open SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", AppCompatActivity.MODE_PRIVATE)
        //Use the editor instance to change the data in shared preferences
        val editor = sharedPreferences.edit()
        //Set the boolean to false as the user as logged out
        editor.putBoolean("isLoggedIn", false)
        //apply the changes
        editor.apply()

        //Take the user back to the Login screen
        val intent = Intent(requireContext(), Login::class.java)
        startActivity(intent)
        activity?.finish()

        //Inform the user that they are being logged out
        Toast.makeText(context, "Logging out...", Toast.LENGTH_SHORT).show()
    }

    //Function that checks if the user is an admin and then chooses whether to display admin option
    fun checkAdminStatusAndShowLayout(context: Context, frameLayout: FrameLayout)
    {
        // Get the current user's UID
        val currentUser = FirebaseAuth.getInstance().currentUser

        //Check if the user isn't null
        if (currentUser != null)
        {
            //Save the uid
            val uid = currentUser.uid

            //Get a reference to the Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()
            val userRef = database.getReference("users").child(uid)

            //Read the value of "isAdmin"
            userRef.child("isAdmin").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val isAdmin = snapshot.getValue(Boolean::class.java) ?: false

                    //Check if user is an admin
                    if (isAdmin)
                    {
                        //Show the FrameLayout if the user is an admin
                        flAdmin.visibility = View.VISIBLE
                    }
                }

                //Handle any potential errors
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to retrieve admin status: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(context, "No authenticated user found.", Toast.LENGTH_SHORT).show()
        }
    }

}