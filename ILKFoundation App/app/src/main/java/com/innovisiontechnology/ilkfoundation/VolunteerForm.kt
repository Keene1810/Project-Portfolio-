package com.innovisiontechnology.ilkfoundation

import android.app.DatePickerDialog
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.GrammaticalInflectionManagerCompat.GrammaticalGender
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import java.util.Locale

class VolunteerForm : AppCompatActivity() {

    // Variables

    //Personal Details section
    private lateinit var edName: EditText
    private lateinit var edSurname: EditText
    private lateinit var edSelectDate: EditText
    private lateinit var spnrGender: Spinner
    private lateinit var edEmail: EditText
    private lateinit var edPhone: EditText
    private lateinit var edAddress: EditText
    //Skills and Experience section
    private lateinit var edVolExperience: EditText
    private lateinit var edSpclSkills: EditText
    //Preferences section
    private lateinit var edIntrstVolunActs: EditText
    //Emergency Contacts section
    private lateinit var edEmrgCtcName: EditText
    private lateinit var edEmrgCtcRltsp: EditText
    private lateinit var edEmrgCtcNum: EditText
    private lateinit var edEmrgCtcNameTwo: EditText
    private lateinit var edEmrgCtcRltspTwo: EditText
    private lateinit var edEmrgCtcNumTwo: EditText
    //Health and Safety section
    private lateinit var edHealthSafety: EditText
    //Legal and Consent section
    private lateinit var spnrCrime: Spinner
    private lateinit var btnSubmit: Button
    //globals -- checks none are nulls
    private var date: Date? = null
    //Spinner choices
    private lateinit var selectedGender: String
    private lateinit var criminalRecord: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer_form)

        // Typecast the variables

        //Personal Details Section
        edName = findViewById(R.id.edName)
        edSurname = findViewById(R.id.edSurname)
        edSelectDate = findViewById(R.id.edSelectDate)
        edEmail = findViewById(R.id.edEmail)
        edPhone = findViewById(R.id.edPhone)
        edAddress = findViewById(R.id.edAddress)
        spnrGender = findViewById(R.id.spnrGender)
        //Skills and Experience section
        edVolExperience = findViewById(R.id.edVolExperience)
        edSpclSkills = findViewById(R.id.edSpclSkills)
        //Preferences section
        edIntrstVolunActs = findViewById(R.id.edIntrstVolunActs)
        //Emergency Contacts section
        edEmrgCtcName = findViewById(R.id.edEmrgCtcName)
        edEmrgCtcRltsp = findViewById(R.id.edEmrgCtcRltsp)
        edEmrgCtcNum = findViewById(R.id.edEmrgCtcNum)
        edEmrgCtcNameTwo = findViewById(R.id.edEmrgCtcNameTwo)
        edEmrgCtcRltspTwo = findViewById(R.id.edEmrgCtcRltspTwo)
        edEmrgCtcNumTwo = findViewById(R.id.edEmrgCtcNumTwo)
        //Health and Safety
        edHealthSafety = findViewById(R.id.edHealthSafety)
        spnrCrime = findViewById(R.id.spnrCrime)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Gender spinner

        //List of items for the spinner
        val genders = listOf("Select Gender:", "Male", "Female")
        //Create an ArrayAdapter
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genders)
        //Specify the layout to use
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Apply the adapter to the Spinner
        spnrGender.adapter = adapter

        //What to do if an item is selected
        spnrGender.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                //Skip the "Select Gender" option
                if (position > 0)
                {
                    val selectedGenOption = parent.getItemAtPosition(position).toString()
                    selectedGender = selectedGenOption
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        // Crime Spinner

        //List of items for the spinner
        val options = listOf("No", "Yes")
        //Create an ArrayAdapter
        val adapterTwo = ArrayAdapter(this, android.R.layout.simple_spinner_item, options)
        //Specify the layout to use
        adapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //Apply the adapter to the Spinner
        spnrCrime.adapter = adapterTwo

        //What to do if an item is selected
        spnrCrime.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                //Save the choice to a value
                val selectedOption = parent.getItemAtPosition(position).toString()
                criminalRecord = selectedOption
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        //Function call to fetch the user's entered email address
        fetchUserEmail()

        //Click events
        edSelectDate.setOnClickListener {
            showDatePicker(dateListener)
        }

        btnSubmit.setOnClickListener {
            //Function call to check if any details are missing
            checkDetails()

        }
    }

    //Function to fetch the user's entered email address
    private fun fetchUserEmail() {
        //Get the current user and save it
        val currentUser = FirebaseAuth.getInstance().currentUser
        //Check if the saved value is null
        if (currentUser != null) {
            //Set the user's email to the email EditText
            edEmail.setText(currentUser.email)
        }
    }

    //Function to show the date picker
    fun showDatePicker(dateSetListener: DatePickerDialog.OnDateSetListener) {
        //Initialize calendar with current year, month, and day
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        //Create and show DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener, year, month, day
        )
        datePickerDialog.show()
    }

    //Date listener
    val dateListener =
        DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->
            val selectedCalendar = Calendar.getInstance()
            selectedCalendar.set(year, month, day)
            date = selectedCalendar.time

            //Format date as "11 November 2024"
            val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
            val selectedDateString = dateFormat.format(date!!)

            //Set the formatted date string to the button text
            edSelectDate.setText(selectedDateString)
        }

    //Function to check if any details are missing
    private fun checkDetails()
    {
        //List that stores all fields
        val fields = listOf(
            edName.text,
            edSurname.text,
            edSelectDate.text,
            selectedGender,
            edEmail.text,
            edPhone.text,
            edVolExperience.text,
            edSpclSkills.text,
            edIntrstVolunActs.text,
            edEmrgCtcName.text,
            edEmrgCtcRltsp.text,
            edEmrgCtcNum.text,
            edEmrgCtcNameTwo.text,
            edEmrgCtcRltspTwo.text,
            edEmrgCtcNumTwo.text,
            edHealthSafety.text,
            criminalRecord
        )

        //Go through each field in the list
        for (field in fields)
        {
            //Check if the field is null or blank
            if (field.isNullOrBlank())
            {
                //Notify the user about incomplete information
                Toast.makeText(this, "Please fill out the remaining info", Toast.LENGTH_SHORT).show()

                //Stop the loop
                return

            } else {
                //Function call to save the details
                saveDetails()
            }
        }
    }

    //Function that saves the application details
    private fun saveDetails() {
        //Personal Details section
        val name = edName.text.toString()
        val surname = edSurname.text.toString()
        val birthDate = edSelectDate.text.toString()
        val gender = selectedGender
        val email = edEmail.text.toString()
        val phone = edPhone.text.toString()
        val address = edAddress.text.toString()
        //Skills and Experience section
        val volExperience = edVolExperience.text.toString()
        val spclSkills = edSpclSkills.text.toString()
        //Preferences section
        val interested = edIntrstVolunActs.text.toString()
        //Emergency Contact section
        val EmergName = edEmrgCtcName.text.toString()
        val EmergRelationship = edEmrgCtcRltsp.text.toString()
        val EmergNumber = edEmrgCtcNum.text.toString()
        val EmergNameTwo = edEmrgCtcNameTwo.text.toString()
        val EmergRelationshipTwo = edEmrgCtcRltspTwo.text.toString()
        val EmergNumberTwo = edEmrgCtcNumTwo.text.toString()
        //Health and Safety section
        val medicalConditions = edHealthSafety.text.toString()
        val crimRecord = criminalRecord

        // Format the email content
        val emailContent = """
        |Name: $name $surname
        |Date of Birth: $birthDate
        |Gender: $gender
        |Email: $email
        |Phone: $phone
        |Address: $address
        |-------------------------
        |Volunteer Experience: $volExperience
        |Special Skills: $spclSkills
        |Interests in Volunteering Activities: $interested
        |-------------------------
        |Emergency Contact 1:
        |Name: $EmergName
        |Relationship: $EmergRelationship
        |Phone Number: $EmergNumber
        |-------------------------
        |Emergency Contact 2:
        |Name: $EmergNameTwo
        |Relationship: $EmergRelationshipTwo
        |Phone Number: $EmergNumberTwo
        |-------------------------
        |Health & Safety: $medicalConditions
        |Legal and Consent: $crimRecord
    """.trimMargin()

        // Use Elastic Email API to send the email
        val apiKey = "818C3084F40712F9203C7C810CB90FB1F044B694DD1FCE43154FC59316F9AE07D6D567548F847896DC9D1B6CB5140168"
        val from = "diegobandle1@gmail.com"
        val subject = "New Volunteer Application from $name $surname"
        val to = "diegobandle1@gmail.com"

        // Make the network call
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetroIns.someInterface.sendEmail(
                    apiKey,
                    from,
                    to,
                    subject,
                    emailContent
                )

                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@VolunteerForm,"Application sent successfully!",Toast.LENGTH_SHORT).show()
                        Log.d("EmailSending", "Sending email now...")

                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@VolunteerForm,"Application not sent",Toast.LENGTH_SHORT).show()
                        Log.d("EmailSending", "Failed to send email. Response code: ${response.code()}")

                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@VolunteerForm, "Error: ${e.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}