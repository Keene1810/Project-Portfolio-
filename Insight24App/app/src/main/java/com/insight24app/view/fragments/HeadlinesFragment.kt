package com.insight24app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.insight24app.R
import com.insight24app.adapters.NewsAdapter
import com.insight24app.databinding.FragmentHeadlinesBinding
import com.insight24app.model.Article
import com.insight24app.view.activity.NewsActivity
import com.insight24app.view.viewmodel.NewsViewModel
import com.insight24app.constant.Constants
import com.insight24app.enums.ViewState
import com.insight24app.util.Resource

class HeadlinesFragment : Fragment() {

    lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: FragmentHeadlinesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeadlinesBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = (activity as NewsActivity).newsViewModel

        context?.let {
            newsViewModel.initialise(it)
        }


        newsViewModel.throwErrorSnackbar.observe(viewLifecycleOwner) {
            showErrorMessage(it)
        }

        newsViewModel.viewState.observe(viewLifecycleOwner) {
            when (it) {
                ViewState.SUCCESS -> {
                    newsViewModel.articles.observe(viewLifecycleOwner) { articles ->
                        val adapter = NewsAdapter(articles, { article ->
                            val bundle = Bundle().apply {
                                putSerializable("article", article)
                            }

                            findNavController().navigate(
                                R.id.action_headlinesFragment2_to_articleFragment,
                                bundle
                            )
                        }, { article -> // This lambda is for the removal functionality
                            newsViewModel.deleteArticle(article)
                        })

                        binding.recyclerHeadlines.adapter = adapter
                        binding.textView.visibility = View.VISIBLE
                        binding.recyclerHeadlines.visibility = View.VISIBLE
                        binding.paginationProgressBar.visibility = View.GONE
                    }
                }

                ViewState.LOADING -> {
                    binding.textView.visibility = View.GONE
                    binding.recyclerHeadlines.visibility = View.GONE
                    binding.paginationProgressBar.visibility = View.VISIBLE
                }

                else -> Unit
            }
        }
    }

    private fun showErrorMessage(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                context?.let {
                    newsViewModel.initialise(it)
                }
            }
            .show()
    }
}
