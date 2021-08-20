package app.wallfact.integration.pixabay.client

import app.wallfact.integration.pixabay.model.PixabayResponse
import app.wallfact.integration.pixabay.repo.PixabayApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

class PixabayClient(private val client: HttpClient, private val baseUrl: String) : PixabayApi {
    override suspend fun getImages(count: Int, query: String): PixabayResponse {
        return client.get("$baseUrl&q=${urlEncode(query)}&count=$count&image_type=photo")
    }

    override suspend fun getImage(query: String): PixabayResponse {
        return client.get("$baseUrl&q=${urlEncode(query)}&image_type=photo")
    }

    override suspend fun getRandomImage(): PixabayResponse {
        return client.get("$baseUrl&image_type=photo")
    }

    private fun urlEncode(str: String) = URLEncoder.encode(str, UTF_8.name())
}