package com.insight24app.view.activity

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.insight24app.R
import com.insight24app.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

      register()

        signInNav()

    }
    private fun register(){
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.regButton.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val userName = binding.username.text.toString()
            val password = binding.password.text.toString()
            val confirmPassword = binding.confirmPassword.text.toString()

            if (email.isNotEmpty() && userName.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {

                if (password == confirmPassword) {
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                Toast.makeText(this, "Successfully created an account", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, Login::class.java)
                                startActivity(intent)

                            } else {
                                Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                                    .show()

                            }
                        }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun signInNav() {
        // Find the TextView
        val signInTextView = findViewById<TextView>(R.id.signInText)

        // Create a SpannableString to underline and make the "Sign In" part clickable
        val spannableString = SpannableString("Already have an account? Sign In")

        // Set underline for "Sign In"
        val signInStart = 25 // Index where "Sign In" starts
        val signInEnd = 32   // Index where "Sign In" ends

        spannableString.setSpan(
            UnderlineSpan(),
            signInStart,
            signInEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set text color for "Sign In"
        spannableString.setSpan(
            ForegroundColorSpan(Color.RED),
            signInStart,
            signInEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set clickable span for "Sign In"
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                // Redirect to Login Activity
                val intent = Intent(this@Register, Login::class.java)
                startActivity(intent)
                finish() // Close the current activity
            }
        }
        spannableString.setSpan(
            clickableSpan,
            signInStart,
            signInEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Assign the spannable text to the TextView
        signInTextView.text = spannableString

        // Enable clicking on the text
        signInTextView.movementMethod = android.text.method.LinkMovementMethod.getInstance()

    }
}