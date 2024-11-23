package com.insight24app.view.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.insight24app.R
import com.insight24app.enums.ViewState
import com.insight24app.model.Article
import com.insight24app.model.NewsResponse
import com.insight24app.repository.NewsRepository
import com.insight24app.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(app: Application, val newsRepository: NewsRepository) : AndroidViewModel(app) {

    private val _throwErrorSnackbar = MutableLiveData<String>()
    val throwErrorSnackbar: LiveData<String> = _throwErrorSnackbar

    private val _viewState = MutableLiveData<ViewState>()
    val viewState: LiveData<ViewState> = _viewState

    private val _articles = MutableLiveData<List<Article>>()
    val articles: LiveData<List<Article>> = _articles

    private val _searchArticles = MutableLiveData<List<Article>>()
    val searchArticles: LiveData<List<Article>> = _searchArticles

    private var hasInternet = false

    fun initialise(context: Context) {
        checkInternetConnection(context)
        //offline with sync
        observeNetworkChanges()

        if (hasInternet) {
            getHeadlines()
        }
    }

    fun searchHeadlines(query: String, context: Context) {
        _viewState.postValue(ViewState.LOADING)

        val searchPage = 1

        if (hasInternet) {

            viewModelScope.launch {
              val response =  newsRepository.searchNews(query, searchPage)

               if (response.isSuccessful) {
                   if (response.body()?.articles != null) {
                       _searchArticles.postValue(response.body()?.articles)
                       _viewState.postValue(ViewState.SUCCESS)
                   }
               }
            }

        } else {
            handleInternetError(context)
        }
    }

    private fun getHeadlines() = viewModelScope.launch {
        _viewState.postValue(ViewState.LOADING)

        val response = newsRepository.getHeadlines(COUNTRY_CODE, HEADLINES_PAGE)

        if (response.isSuccessful) {
            response.body()?.articles?.let {
                _viewState.postValue(ViewState.SUCCESS)
                _articles.postValue(it)
            }
        }
    }

    private fun checkInternetConnection(context: Context) {
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            getNetworkCapabilities(activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> hasInternet = true
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> hasInternet = true
                    hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> hasInternet = true
                    else -> handleInternetError(context)
                }
            } ?: handleInternetError(context)
        }
    }

    private fun handleInternetError(context: Context) {
        hasInternet = false
        _throwErrorSnackbar.postValue(context.getString(R.string.error_no_internet_connection))
    }

    fun addToFavourites(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getFavouriteNews() = newsRepository.getFavouriteNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    companion object {
        private const val COUNTRY_CODE = "us"
        private const val HEADLINES_PAGE = 1
    }



// offline
    private val connectivityManager =
        getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun observeNetworkChanges() {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                syncFavoriteArticles()
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun syncFavoriteArticles() = viewModelScope.launch {
        val favorites = newsRepository.getFavouriteNews().value ?: return@launch
        for (article in favorites) {
            // Check if the article is still valid in the API (you can adjust the logic based on your needs)
            val response = newsRepository.getHeadlines(COUNTRY_CODE, HEADLINES_PAGE) // Adjust this as needed
            response.body()?.articles?.find { it.id == article.id }?.let {
                // Article is still valid in the API, you might want to update local data
                newsRepository.upsert(it)
            }
        }
    }

}
