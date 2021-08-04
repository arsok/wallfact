package app.wallfact.integration.pixabay.api

interface PixabayRepoApi {
    suspend fun getImages(count: Int, query: String = "")
    suspend fun getImage(query: String = "")
    suspend fun getRandomImage()
}