package app.wallfact.integration.pixabay.repo

import app.wallfact.integration.pixabay.client.PixabayClient

class PixabayRepo(private val pixabayClient: PixabayClient) : PixabayApi {

    override suspend fun getImages(count: Int, query: String) = pixabayClient.getImages(count, query)

    override suspend fun getImage(query: String) = pixabayClient.getImage(query)

    override suspend fun getRandomImage() = pixabayClient.getRandomImage()
}