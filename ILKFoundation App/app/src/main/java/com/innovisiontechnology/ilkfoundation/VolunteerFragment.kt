package com.innovisiontechnology.ilkfoundation

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView


class VolunteerFragment : Fragment() {

    //Variables
    lateinit var tvWhyVol: TextView
    lateinit var btnVolunteer: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_volunteer, container, false)

        //Typecast the variables
        tvWhyVol = view.findViewById(R.id.tvWhyVol)
        btnVolunteer = view.findViewById(R.id.btnVolunteer)

        //Function call to add colours to tvWhyVol
        addMultipleColours()

        //btn click events

        //Go to the Volunteer Form screen when the volunteer btn is clicked
        btnVolunteer.setOnClickListener {
            val intent = Intent(requireContext(), VolunteerForm::class.java)
            startActivity(intent)
        }

        return view
    }

    //Function that adds colours to tvWhyVol
    private fun addMultipleColours()
    {
        //Create a SpannableStringBuilder
        val builder = SpannableStringBuilder()

        //Append
        builder.append("Why ")

        //Append "Vol" with colour
        val Vol = builder.length
        builder.append("Vol")
        builder.setSpan(ForegroundColorSpan(Color.rgb(114, 162, 29)), Vol, builder.length, 0)

        //Append "unt" with colour
        val unt = builder.length
        builder.append("unt")
        builder.setSpan(ForegroundColorSpan(Color.rgb(120,73,53)), unt, builder.length, 0)

        //Append "eer" with colour
        val eer = builder.length
        builder.append("eer")
        builder.setSpan(ForegroundColorSpan(Color.rgb(255, 195, 1)), eer, builder.length, 0)

        //Append the rest of the text
        builder.append(" With Us?")

        //Apply the SpannableStringBuilder to the TextView
        tvWhyVol.text = builder
    }
}