package app.wallfact.integration.unsplash.repo

import app.wallfact.integration.unsplash.client.UnsplashClient

class UnsplashRepo(private val unsplashClient: UnsplashClient) : UnsplashApi {

    override suspend fun listImages(count: Int) = unsplashClient.listImages(count)

    override suspend fun searchImages(count: Int, query: String) = unsplashClient.searchImages(count, query)

    override suspend fun getRandomImage() = unsplashClient.getRandomImage()
}