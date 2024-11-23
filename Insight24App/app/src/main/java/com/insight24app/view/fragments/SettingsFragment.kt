package com.insight24app.view.fragments

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.insight24app.R
import com.insight24app.view.activity.Login
import java.util.Locale

class SettingsFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private var currentLang: String = "en"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Initialize Language Spinner and Theme Switch
        setupLanguageSpinner(view)
        setupThemeSwitch(view)
        setupLogoutButton(view)

        return view
    }

    // Function to handle the logout button setup and listener
    private fun setupLogoutButton(view: View) {
        val logoutButton: Button = view.findViewById(R.id.logoutButton)

        // Set the logout button click listener
        logoutButton.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        auth.signOut() // Sign out from Firebase Authentication

        // Navigate to the login activity
        val intent = Intent(activity, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        activity?.finish() // Close the current activity
    }

    // Function to handle the theme switch setup and listener
    private fun setupThemeSwitch(view: View) {
        val themeSwitch: Switch = view.findViewById(R.id.themeSwitch)

        // Load the saved theme preference
        val sharedPref = activity?.getSharedPreferences("ThemePref", Context.MODE_PRIVATE)
        val isDarkMode = sharedPref?.getBoolean("isDarkMode", false) ?: false
        themeSwitch.isChecked = isDarkMode

        // Set the switch listener
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            setDarkMode(isChecked)
        }
    }

    private fun setDarkMode(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        saveThemePreference(isDarkMode)
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        val sharedPref = activity?.getSharedPreferences("ThemePref", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean("isDarkMode", isDarkMode)
            apply()
        }
    }

    // Function to handle the language spinner setup
    private fun setupLanguageSpinner(view: View) {
        val spinner: Spinner = view.findViewById(R.id.spinner)
        val languages = arrayOf("English", "Afrikaans", "Zulu")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set the current language
        currentLang = Locale.getDefault().language

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> setLocale("en") // English
                    1 -> setLocale("af")
                    2 -> setLocale("zu")

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No action needed
            }
        }
    }

    // Function to change the app language
    private fun setLocale(lang: String) {
        if (lang != currentLang) {
            currentLang = lang

            val locale = Locale(lang)
            Locale.setDefault(locale)
            val config = Configuration()
            config.setLocale(locale)
            requireActivity().resources.updateConfiguration(config, requireActivity().resources.displayMetrics)

            // Refresh fragment
            requireActivity().recreate()
        }
    }
}
