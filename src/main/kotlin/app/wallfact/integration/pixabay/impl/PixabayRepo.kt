package app.wallfact.integration.pixabay.impl

import app.wallfact.integration.pixabay.api.PixabayRepoApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class PixabayRepo : PixabayRepoApi {
    private val client = HttpClient()

    override suspend fun getImages(count: Int, query: String) {
        TODO()
    }

    override suspend fun getImage(query: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getRandomImage() {
        TODO("Not yet implemented")
    }

    suspend fun testKtorClient(): HttpResponse {
        return client.get("https://reqbin.com/echo")
    }
}