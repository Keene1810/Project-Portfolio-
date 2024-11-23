package com.insight24app.view.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.insight24app.R
import com.insight24app.adapters.HeadlinesAdapter
import com.insight24app.api.RetrofitClient
import com.insight24app.model.InsightResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeekHeadlinesFragment : Fragment(R.layout.fragment_weeks_headlines) {

    private lateinit var headlinesAdapter: HeadlinesAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewHeadlines)
        headlinesAdapter = HeadlinesAdapter()

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = headlinesAdapter
        }

        fetchHeadlines()
    }

    private fun fetchHeadlines() {
        RetrofitClient.api.getHeadlines().enqueue(object : Callback<InsightResponse> {
            override fun onResponse(
                call: Call<InsightResponse>,
                response: Response<InsightResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        headlinesAdapter.submitList(it.record)
                    }
                }
            }

            override fun onFailure(call: Call<InsightResponse>, t: Throwable) {
                // Handle error
            }
        })
    }
}
