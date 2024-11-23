package com.insight24app.repository

import com.insight24app.api.RetrofitInstance
import com.insight24app.db.ArticleDatabase
import com.insight24app.model.Article

class NewsRepository(val db: ArticleDatabase) {
    
    suspend fun getHeadlines(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getHeadlines(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getFavouriteNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deteleArticle(article)


}