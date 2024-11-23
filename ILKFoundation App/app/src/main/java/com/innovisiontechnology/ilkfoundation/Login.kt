package com.innovisiontechnology.ilkfoundation

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.klinker.android.link_builder.Link
import com.klinker.android.link_builder.applyLinks

class Login : AppCompatActivity() {

    //variables
    lateinit var tvSignUp: TextView
    lateinit var btnSignIn: Button
    lateinit var edEmail: EditText
    lateinit var edPassword: EditText
    //firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        //typecast the variables
        tvSignUp = findViewById(R.id.tvSignUp)
        btnSignIn = findViewById(R.id.btnLogin)
        edEmail = findViewById(R.id.edtEmail)
        edPassword = findViewById(R.id.edtPassword)

        //initialize firebase
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()

        //btn click events
        btnSignIn.setOnClickListener {
            val email = edEmail.text.toString().trim()
            val password = edPassword.text.toString().trim()

            //error checks
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter valid details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }//if ends
            loginUser(email, password)
        }

        tvSignUp.setOnClickListener {
            val intentReg = Intent(this@Login, Register::class.java)
            startActivity(intentReg)}

        //function call
        linkSetUp()
    }

    //Function that attempts to log the user in
    private fun loginUser(email:String, password:String)
    {
        //Create a ProgressDialog
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Logging you in...")
        progressDialog.setCancelable(false)
        //Show the ProgressDialog
        progressDialog.show()

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener(this) {task ->
                if (task.isSuccessful)
                {
                    // Create/ open a SharedPreferences and keep it private
                    val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    //To change the data in SharedPreferences you need an editor instance
                    val editor = sharedPreferences.edit()
                    //Add a boolean and set it to true as the user is now logged in
                    editor.putBoolean("isLoggedIn", true)
                    //Save the changes that were made
                    editor.apply()

                    //Function call to check if the user is an admin
                    checkAdminStatus(this)

                    //Close the ProgressDialog
                    progressDialog.dismiss()

                    //Move to the main screen
                    val intent = Intent(this@Login, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                } else
                {
                    //Inform the user when the Login has failed
                    Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show()

                    //Close the ProgressDialog
                    progressDialog.dismiss()
                }
            }
    }

    //Function for the sign up link
    private fun linkSetUp()
    {
        val signUpLink = Link("Sign Up")
            .setTextColor(Color.parseColor("#ffc838"))
            .setHighlightAlpha(.4f)
            .setUnderlined(false)
            .setBold(true)
            .setOnClickListener{
                val intentLog = Intent(this@Login,Register::class.java)
                startActivity(intentLog)
            }
        tvSignUp.applyLinks(signUpLink)
    }

    //Function that checks if the user is an admin and then chooses whether to display admin options
    private fun checkAdminStatus(context: Context)
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

                    //Check if user is admin
                    if (isAdmin)
                    {
                        Toast.makeText(context, "Welcome, admin!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context,"Logged in successfully", Toast.LENGTH_SHORT).show()
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