package com.innovisiontechnology.ilkfoundation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.core.content.ContentProviderCompat.requireContext

class AdminFeatures : AppCompatActivity() {

    //Variables
    private lateinit var flCreateEvent: FrameLayout
    private lateinit var flDeleteEvent: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_features)

        //Typecast the variables
        flCreateEvent = findViewById(R.id.flCreateEvent)
        flDeleteEvent = findViewById(R.id.flDeleteEvent)

        // onClickEvents

        flCreateEvent.setOnClickListener {
            //Open the Create Event screen
            val bottomSheet = CreateEventFragment()
            bottomSheet.show(supportFragmentManager, "FragmentCreateEvent")
        }

        flDeleteEvent.setOnClickListener {
            //Open the Select Event to Delete Screen
            val bottomSheet = SelectEventToDeleteFragment()
            bottomSheet.show(supportFragmentManager, "FragmentSelectEventToDelete")
        }

    }

}