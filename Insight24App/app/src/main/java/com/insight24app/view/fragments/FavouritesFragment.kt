package com.insight24app.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.navigation.fragment.findNavController
import com.insight24app.R
import com.insight24app.adapters.NewsAdapter
import com.insight24app.databinding.FragmentFavouritesBinding
import com.insight24app.model.Article
import com.insight24app.view.activity.NewsActivity
import com.insight24app.view.viewmodel.NewsViewModel

class FavouritesFragment : Fragment() {

    lateinit var newsViewModel: NewsViewModel
    private lateinit var binding: FragmentFavouritesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsViewModel = (activity as NewsActivity).newsViewModel
        binding.backButton.setOnClickListener {
            findNavController().navigateUp() // This will navigate back to the previous screen
        }
        setupFavouritesRecycler()
    }

    private fun setupFavouritesRecycler() {
        newsViewModel.getFavouriteNews().observe(viewLifecycleOwner) { articles ->
            val adapter = NewsAdapter(articles, { article ->
                val bundle = Bundle().apply {
                    putSerializable("article", article)
                }
                findNavController().navigate(
                    R.id.action_favouritesFragment2_to_articleFragment,
                    bundle
                )
            }, { article ->
                showRemoveConfirmationDialog(article)
            })

            binding.recyclerFavourites.adapter = adapter
        }
    }

    private fun showRemoveConfirmationDialog(article: Article) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle("Remove Article")
            .setMessage("Are you sure you want to remove this article from favorites?")
            .setPositiveButton("Yes") { dialog, which ->
                newsViewModel.deleteArticle(article)
                Toast.makeText(requireContext(), "Article removed from favorites.", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            .create()
        alertDialog.show()
    }
}
