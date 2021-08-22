package app.wallfact.integration.unsplash.repo

import app.wallfact.integration.unsplash.model.UnsplashImage
import app.wallfact.integration.unsplash.model.UnsplashSearchResponse

interface UnsplashApi {
    suspend fun listImages(count: Int): List<UnsplashImage>
    suspend fun searchImages(count: Int, query: String): UnsplashSearchResponse
    suspend fun getRandomImage(): UnsplashImage
}