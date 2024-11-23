package com.innovisiontechnology.ilkfoundation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import java.util.Timer
import java.util.TimerTask

class Splash : AppCompatActivity() {

    //variables
    lateinit var ivSplash: ImageView
    //set the delay before the screen go to the next screen
    private val delay: Long = 5600

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        //typecast the variables
        ivSplash = findViewById(R.id.ivSplash)

        //animated image code
        Glide.with(this).load(R.drawable.ilksplash).into(ivSplash)

        //create a timer object
        val runSplash = Timer()

        //Task to do after
        val showSplash = object : TimerTask()
        {
            override fun run()
            {
                //will close the main
                //finish()

                // Create/ open a SharedPreferences and keep it private
                val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                //read the value, it returns false by default
                val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

                if (isLoggedIn) {
                    // move to the main screen
                    val intent = Intent(this@Splash, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // move to the Login screen
                    val intent = Intent(this@Splash, Login::class.java)
                    startActivity(intent)
                    finish() // Finish the splash screen activity to prevent it from being in the back stack
                }
                finish() // Make sure the splash activity finishes after starting the next activity
            }

        }//object ends
        runSplash.schedule(showSplash,delay)
    }
}