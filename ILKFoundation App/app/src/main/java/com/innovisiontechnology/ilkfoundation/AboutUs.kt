package com.innovisiontechnology.ilkfoundation

import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.Button
import com.google.android.material.card.MaterialCardView
import androidx.appcompat.app.AppCompatActivity

class AboutUs : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var btnPrivacy: Button
    private lateinit var logo: MaterialCardView
    private lateinit var missionCard: MaterialCardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)

        // Initialize views
        webView = findViewById(R.id.privacyWebView)
        btnPrivacy = findViewById(R.id.privacyPolicyButton)
        logo = findViewById(R.id.logoCard)
        missionCard = findViewById(R.id.missionCard)

        // Enable JavaScript
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true

        // Handle Privacy Policy Button click
        btnPrivacy.setOnClickListener {
            // Load the Privacy Policy URL
            val privacyPolicyUrl = "https://www.freeprivacypolicy.com/live/8144536c-12d6-4417-bfa0-e0a0cb0c2532"
            webView.loadUrl(privacyPolicyUrl)
            webView.visibility = View.VISIBLE
            logo.visibility = View.GONE
            missionCard.visibility = View.GONE
            btnPrivacy.visibility = View.GONE
        }
    }

    // Override onBackPressed to handle back press behavior
    override fun onBackPressed() {
        // Check if WebView is visible
        if (webView.visibility == View.VISIBLE) {
            // Hide the WebView and restore visibility of other views
            webView.visibility = View.GONE
            logo.visibility = View.VISIBLE
            missionCard.visibility = View.VISIBLE
            btnPrivacy.visibility = View.VISIBLE
        } else {
            // Call the default back press behavior
            super.onBackPressed()
        }
    }
}
