package com.insight24app.view.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.insight24app.R
import java.util.Timer
import java.util.TimerTask

class Splash : AppCompatActivity() {

    lateinit var imageView : ImageView
    val delay :Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        imageView = findViewById(R.id.splash)
        // animated image code
        Glide.with(this).load(R.drawable.splash).into(imageView)
        // create a timer obj
        val runSplash = Timer()
        // task to do after
        val showSplash =object : TimerTask()
        {
            override fun run(){
                // will close main
                finish()
                // move to next screen
                val intentOne = Intent(this@Splash,Login::class.java)
                startActivity(intentOne)

            } // run method ends
        } // object ends
        runSplash.schedule(showSplash,delay)
    }
}