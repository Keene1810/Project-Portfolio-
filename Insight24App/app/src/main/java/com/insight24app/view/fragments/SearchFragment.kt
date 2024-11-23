package com.insight24app.view.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.insight24app.adapters.NewsAdapter
import com.insight24app.databinding.FragmentSearchBinding
import com.insight24app.view.activity.NewsActivity
import com.insight24app.view.viewmodel.NewsViewModel
import com.insight24app.enums.ViewState

class SearchFragment : Fragment() {
    private lateinit var binding: FragmentSearchBinding
    lateinit var newsViewModel: NewsViewModel
    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = (activity as NewsActivity).newsViewModel

        binding.backButton.setOnClickListener {
            findNavController().navigateUp() // This will navigate back to the previous screen
        }

        binding.searchEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                context?.let {
                    newsViewModel.searchHeadlines(s.toString(), it)
                }
            }
        })

        newsViewModel.apply {
            viewState.observe(viewLifecycleOwner) {
                when (it) {
                    ViewState.LOADING -> {
                        binding.recyclerSearch.visibility = View.GONE
                        binding.paginationProgressBar.visibility = View.VISIBLE
                    }

                    ViewState.SUCCESS -> {

                        newsViewModel.searchArticles.observe(viewLifecycleOwner) { articles ->
                            adapter = NewsAdapter(articles, { article ->
                            }, { article -> // This lambda is for the removal functionality
                                newsViewModel.deleteArticle(article)
                            })

                            binding.recyclerSearch.adapter = adapter
                            binding.recyclerSearch.visibility = View.VISIBLE
                            binding.paginationProgressBar.visibility = View.GONE
                        }
                    }

                    else -> Unit
                }
            }
        }
    }
}
