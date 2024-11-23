package com.insight24app.view.activity

import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.insight24app.R
import com.insight24app.adapters.HeadlinesAdapter
import com.insight24app.model.InsightHeadlines
import com.insight24app.util.NetworkChangeReceiver

class Offline : AppCompatActivity(), NetworkChangeReceiver.NetworkChangeCallback {

    private lateinit var headlinesAdapter: HeadlinesAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var networkChangeReceiver: NetworkChangeReceiver


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline)

        recyclerView = findViewById(R.id.offlineRecycler2)
        headlinesAdapter = HeadlinesAdapter()

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@Offline)
            adapter = headlinesAdapter
        }


        val hardcodedArticles = getHardcodedArticles()
        headlinesAdapter.submitList(hardcodedArticles)

        // Register the NetworkChangeReceiver
        networkChangeReceiver = NetworkChangeReceiver(this)
        registerReceiver(networkChangeReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkChangeReceiver)
    }

    override fun onNetworkRestored() {
        showNetworkRestoredDialog()
    }

    private fun showNetworkRestoredDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setMessage("Internet connection has been restored. Would you like to return to the normal view?")
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                // Navigate back to the main activity (replace NewsActivity with your main activity)
                startActivity(Intent(this, NewsActivity::class.java))
                finish()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }

        val alert = dialogBuilder.create()
        alert.setTitle("Network Status")
        alert.show()
    }


    private fun getHardcodedArticles(): List<InsightHeadlines> {
        return listOf(
            InsightHeadlines(1, "Global Economy Rebounds in Q3", "The global economy has shown strong signs of recovery...", "2024-08-15"),
            InsightHeadlines(2, "Advances in AI Technology", "New breakthroughs in artificial intelligence have...", "2024-09-05"),
            InsightHeadlines(3, "Climate Change Action Summit 2024", "World leaders gathered to address the ongoing climate crisis...", "2024-10-01"),
            InsightHeadlines(4, "Senate Passes Landmark Climate Change Legislation", "The U.S. Senate approved a sweeping bill to combat climate change, marking a historic victory for environmental advocates.", "2024-10-22"),
            InsightHeadlines(5, "Tech Giants Face Scrutiny Over AI Regulations", "Major U.S. tech companies are under pressure as new regulations aim to curb unchecked AI development and protect user privacy.", "2024-10-22"),
            InsightHeadlines(6, "NASA Successfully Launches Artemis III Mission", "NASA's Artemis III mission launched successfully, aiming to land the next astronauts on the moon, including the first woman.", "2024-10-21"),
            InsightHeadlines(7, "Stock Market Dips Amid Economic Uncertainty", "Wall Street saw a sharp decline as fears of a potential economic downturn loom following inflation reports.", "2024-10-21"),
            InsightHeadlines(8, "Supreme Court to Hear Major Gun Control Case", "The U.S. Supreme Court will soon hear arguments in a case that could determine the future of national gun control laws.", "2024-10-20"),
            InsightHeadlines(9, "California Wildfires Rage, Forcing Thousands to Evacuate", "Wildfires continue to spread across Southern California, prompting mass evacuations and threatening homes and wildlife.", "2024-10-20"),
            InsightHeadlines(10, "New COVID-19 Variant Sparks Worries of a Winter Surge", "Health officials are closely monitoring a new COVID-19 variant as the country prepares for a potential winter surge in cases.", "2024-10-19"),
            InsightHeadlines(11, "Hollywood Strike Ends After 3 Months", "The Writers Guild of America reached an agreement with studios, ending the longest strike in Hollywood's recent history.", "2024-10-19"),
            InsightHeadlines(12, "Biden Announces Infrastructure Funding for Rural Areas", "President Joe Biden unveiled a new infrastructure plan aimed at improving roads, bridges, and broadband access in rural communities.", "2024-10-18"),
            InsightHeadlines(13, "Auto Industry Revolutionized by Electric Vehicles", "The electric vehicle market continues to grow rapidly, with major automakers announcing plans to transition to fully electric fleets by 2030.", "2024-10-18"),
            InsightHeadlines(14, "Record-Breaking Hurricane Makes Landfall in Florida", "A Category 5 hurricane struck Florida's Gulf Coast, setting new records for wind speed and rainfall in the region.", "2024-10-17"),
            InsightHeadlines(15, "U.S. and China Agree to New Trade Deal Framework", "In a surprise move, U.S. and Chinese officials reached a preliminary agreement on trade tariffs after years of economic tension.", "2024-10-17"),
            InsightHeadlines(16, "Healthcare Costs Soar, Putting Pressure on U.S. Families", "A new report reveals that healthcare costs have risen dramatically in the past year, leading to financial strain for millions of Americans.", "2024-10-16"),
            InsightHeadlines(17, "Protests Erupt Over Police Brutality in New York", "Protesters took to the streets in New York City after another high-profile case of police violence sparked outrage.", "2024-10-16"),
            InsightHeadlines(18, "Scientists Discover New Treatment for Alzheimer's", "A groundbreaking study has shown promising results in treating Alzheimer's disease, giving hope to millions affected by the illness.", "2024-10-15"),
            InsightHeadlines(19, "Unemployment Rate Falls to Lowest in Two Decades", "The U.S. unemployment rate fell to 3.1%, the lowest in 20 years, as the economy shows signs of recovery despite global challenges.", "2024-10-15"),
            InsightHeadlines(20, "New York City Marathon Sees Record Participation", "Over 60,000 runners participated in this year's New York City Marathon, setting a new record for the world's largest marathon.", "2024-10-14"),
            InsightHeadlines(21, "Google Unveils New Pixel 9 Smartphone", "Google has officially launched its Pixel 9 smartphone, featuring advanced AI-driven camera features and improved battery life.", "2024-10-14"),
            InsightHeadlines(22, "Federal Reserve Hints at Interest Rate Hike", "The Federal Reserve signaled that an interest rate increase may be imminent as the central bank tries to control rising inflation.", "2024-10-13"),
            InsightHeadlines(23, "Electric Grid Struggles Amid Increasing Demand", "The U.S. power grid is facing unprecedented stress as more homes and businesses adopt renewable energy, causing rolling blackouts in some states.", "2024-10-13")

        )
    }

}