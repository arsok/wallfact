package app.wallfact.integration.pixabay.model

import kotlinx.serialization.Serializable

@Serializable
data class Hit(val id: Int, val pageURL: String, val type: String, val tags: String, val previewURL: String,
               val previewWidth: Int, val previewHeight: Int, val webformatURL: String, val webformatWidth: Int,
               val webformatHeight: Int, val largeImageURL: String, val fullHDURL: String? = null,
               val imageURL: String? = null, val imageWidth: Int, val imageHeight: Int, val imageSize: Int,
               val views: Int, val downloads: Int, val likes: Int, val comments: Int, val userId: Int? = null,
               val user: String, val userImageURL: String)