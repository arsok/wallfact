package app.wallfact.integration.unsplash.model

import kotlinx.serialization.Serializable

@Serializable
data class UnsplashSearchResponse(val total: Int, val totalPages: Int = 1, val results: List<UnsplashImage>)

@Serializable
data class UnsplashImage(val width: Int, val height: Int, val urls: Urls)

@Serializable
data class Urls(val raw: String, val full: String, val regular: String)