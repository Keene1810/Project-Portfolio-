package com.innovisiontechnology.ilkfoundation

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID


class CreateEventFragment : DialogFragment() {

    //Variables
    private lateinit var ivEvent: ImageView
    private lateinit var edEventName: EditText
    private lateinit var edEventDescription: EditText
    private lateinit var edEventLocation: EditText
    private lateinit var btnSelectImage: Button
    private lateinit var btnSelectDate: Button
    private lateinit var btnSelectTime: Button
    private lateinit var btnCreate: Button
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var selectedImageUri: Uri? = null
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    //Firebase Storage ref
    private lateinit var storageReference: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_create_event, container, false)

        //Typecast the variables
        ivEvent = view.findViewById(R.id.ivEvent)
        edEventName = view.findViewById(R.id.edEventName)
        edEventDescription = view.findViewById(R.id.edEventDescription)
        edEventLocation = view.findViewById(R.id.edEventLocation)
        btnSelectImage = view.findViewById(R.id.btnSelectImage)
        btnSelectDate = view.findViewById(R.id.btnSelectDate)
        btnSelectTime = view.findViewById(R.id.btnSelectTime)
        btnCreate = view.findViewById(R.id.btnCreate)

        val storage = FirebaseStorage.getInstance("gs://ilkfoundation.firebasestorage.app")
        storageReference = storage.reference


        //Initialize the image picker launcher
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                //Show selected image in ImageView
                ivEvent.setImageURI(selectedImageUri)
            }
        }

        //Btn Click events
        btnSelectImage.setOnClickListener {
            selectImage()
        }

        btnSelectTime.setOnClickListener {
            showTimePicker(btnSelectTime)
        }

        btnSelectDate.setOnClickListener {
            showDatePicker(btnSelectDate)
        }

        btnCreate.setOnClickListener {
            createEvent()
        }

        return view
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(intent)
    }

    //Function that enables the user to pick a date
    private fun showDatePicker(button: Button)
    {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val dateFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
                selectedDate = dateFormat.format(selectedCalendar.time)
                button.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    //Function that enables the user to pick a time
    private fun showTimePicker(button: Button)
    {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                //Set the selected time
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                button.text = selectedTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            //Use 24-hour format
            true
        )
        timePickerDialog.show()
    }

    private fun createEvent() {
        val eventName = edEventName.text.toString()
        val eventDescription = edEventDescription.text.toString()
        val eventLocation = edEventLocation.text.toString()
        val eventDate = selectedDate ?: ""
        val eventTime = selectedTime ?: ""

        if (selectedImageUri != null) {
            uploadImageToFirebaseStorage(eventName, eventDescription, eventLocation, eventDate, eventTime)
        } else {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    //Function that add the newly created event to Firestore
    private fun uploadImageToFirebaseStorage(eventName: String,eventDescription: String,eventLocation: String,eventDate: String,eventTime: String)
    {
        if (!isConnectedToInternet()) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_LONG).show()
            return
        }

        val progressDialog = ProgressDialog(requireContext())
        progressDialog.setMessage("Uploading data...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        selectedImageUri?.let { uri ->
            val fileRef = storageReference.child("images/${UUID.randomUUID()}.jpg")

            fileRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    fileRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        uploadEventToFirestore(eventName, eventDescription, eventLocation, eventDate, eventTime, downloadUri.toString())
                        progressDialog.dismiss()
                        Toast.makeText(requireContext(), "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                        dismiss()  // Close dialog on success
                    }
                        .addOnFailureListener { e ->
                            progressDialog.dismiss()
                            Toast.makeText(requireContext(), "Failed to get download URL: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), "Image upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = (100L * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                    progressDialog.setMessage("Uploading: $progress%")
                }
        } ?: run {
            progressDialog.dismiss()
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadEventToFirestore(eventName: String,eventDescription: String,eventLocation: String,eventDate: String,eventTime: String,imageUrl: String)
    {
        val firestore = FirebaseFirestore.getInstance()

        val eventData = hashMapOf(
            "name" to eventName,
            "description" to eventDescription,
            "location" to eventLocation,
            "date" to eventDate,
            "time" to eventTime,
            "imageUrl" to imageUrl
        )

        firestore.collection("events")
            .add(eventData)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Event Created Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to upload event: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    //Function to check internet connection status
    private fun isConnectedToInternet(): Boolean
    {
        val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        return capabilities?.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

}