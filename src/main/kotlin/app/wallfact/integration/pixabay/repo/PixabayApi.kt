package app.wallfact.integration.pixabay.repo

import app.wallfact.integration.pixabay.model.PixabayResponse

interface PixabayApi {
    suspend fun getImages(count: Int, query: String = ""): PixabayResponse
    suspend fun getImage(query: String = ""): PixabayResponse
    suspend fun getRandomImage(): PixabayResponse
}