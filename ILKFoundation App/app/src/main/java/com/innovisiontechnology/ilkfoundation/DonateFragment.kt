package com.innovisiontechnology.ilkfoundation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.innovisiontechnology.ilkfoundation.databinding.FragmentDonateBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class DonateFragment : Fragment() {

    // Binding variable to access UI elements in the fragment
    private var _binding: FragmentDonateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using View Binding
        _binding = FragmentDonateBinding.inflate(inflater, container, false)

        // Set up WebView configurations
        setupWebView()
        // Set up donate button click listener
        setupDonateButton()

        return binding.root
    }

    //Configures the WebView for displaying the donation page.
    private fun setupWebView() {
        binding.zapperWebView.apply {
            // Enable JavaScript for the WebView
            settings.javaScriptEnabled = true
            // Enable DOM storage
            settings.domStorageEnabled = true
            // Prevent opening links outside the WebView
            webViewClient = WebViewClient()
        }
    }

    //Sets up the donation button to initiate the payment process.
    private fun setupDonateButton() {
        binding.donateButton.setOnClickListener {
            try {
                // Start payment session when the button is clicked
                initiatePaymentSession()
            } catch (e: Exception) {
                // Log error and display message if payment initiation fails
                Log.e("DonateFragment", "Error initiating payment session", e)
                Toast.makeText(context, "Failed to start donation process. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

     //Initiates the payment session with required details.
    private fun initiatePaymentSession() {
        // Validate and get donation amount
        val amount = validateAndRetrieveAmount() ?: return
        // Set name as "Anonymous" if anonymous switch is checked, otherwise get from EditText
        val name = if (binding.anonymousSwitch.isChecked) "Anonymous" else binding.nameEditText.text.toString().trim()
        // Prepare custom fields and session request for the API call
        val customFields = listOf(CustomField("customer_name", name))
        val sessionRequest = SessionRequest(
            //Generate a unique request ID
            requestId = UUID.randomUUID().toString(),
            // Generate a unique order ID
            merchantOrderId = generateMerchantOrderId(),
            amount = amount,
            currencyISOCode = "ZAR",
            notificationUrl = "https://placeholder-url.com",
            returnUrl = "https://placeholder-url.com",
            cancelUrl = "https://placeholder-url.com",
            origin = "https://placeholder-url.com",
            customFields = customFields
        )
       // Make the API call to create a payment session
        ApiClient.zapperApiService.createSession(
            merchantId = "70151",
            merchantSiteId = "88938",
            apiKey = "46b5fbb8a981467184df7a5680dc73bc",
            request = sessionRequest
        ).enqueue(object : Callback<SessionResponse> {
            override fun onResponse(call: Call<SessionResponse>, response: Response<SessionResponse>) {
                // If response is successful, redirect the user to the payment page
                if (response.isSuccessful) {
                    response.body()?.redirectUrl?.let { redirectUrl ->
                        redirectUserToPaymentPage(redirectUrl)
                    } ?: run {
                        // Log error and show message if redirect URL is null
                        Log.e("DonateFragment", "Redirect URL is null")
                        Toast.makeText(context, "Failed to retrieve payment URL.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Log error and show message if response is not successful
                    val errorMsg = response.errorBody()?.string() ?: "Unknown error"
                    Log.e("DonateFragment", "Response not successful: $errorMsg")
                    Toast.makeText(context, "Failed to initiate payment. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SessionResponse>, t: Throwable) {
                // Log error and show message if the API call fails
                Log.e("DonateFragment", "API call failed", t)
                Toast.makeText(context, "Network error. Please check your connection and try again.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Validates the input amount and converts it to the required format.
    private fun validateAndRetrieveAmount(): Int? {
        val amountText = binding.amountEditText.text.toString()
        return if (amountText.isNotEmpty()) {
            try {
                // Convert amount to cents
                val amount = (amountText.toDouble() * 100).toInt()
                // Display message if amount is non-positive
                if (amount <= 0) {
                    Toast.makeText(context, "Please enter a positive amount.", Toast.LENGTH_SHORT).show()
                    null
                } else {
                    amount
                }
            } catch (e: NumberFormatException) {
                // Display message if amount format is invalid
                Toast.makeText(context, "Invalid amount entered.", Toast.LENGTH_SHORT).show()
                Log.e("DonateFragment", "Amount parsing error", e)
                null
            }
        } else {
            // Display message if amount field is empty
            Toast.makeText(context, "Please enter an amount.", Toast.LENGTH_SHORT).show()
            null
        }
    }
    //Redirects the user to the payment page using the WebView.
    private fun redirectUserToPaymentPage(url: String?) {
        if (url.isNullOrEmpty()) {
            // Log error and show message if the URL is invalid
            Log.e("DonateFragment", "Redirect URL is empty or null")
            Toast.makeText(context, "Payment URL is invalid.", Toast.LENGTH_SHORT).show()
            return
        }
        // Set WebViewClient to handle URL loading within WebView
        binding.zapperWebView.apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    // Handle zapper app URL redirection
                    if (url != null && url.startsWith("zapper://")) {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                        return true
                    }
                    return false
                }
            }
            // Load payment URL
            loadUrl(url)
            // Make WebView visible
            visibility = View.VISIBLE
        }
    }



    //Generates a random merchant order ID.
    private fun generateMerchantOrderId(): String {
        //initalize characters to use in the order id
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        //return a random order id
        return (1..5)
            .map { chars.random() }
            .joinToString("")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear binding reference to prevent memory leaks
        _binding = null
    }
}
