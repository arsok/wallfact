package app.wallfact.integration.unsplash.client

import app.wallfact.integration.unsplash.model.UnsplashImage
import app.wallfact.integration.unsplash.model.UnsplashSearchResponse
import app.wallfact.integration.unsplash.repo.UnsplashApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import org.slf4j.LoggerFactory

class UnsplashClient(
    private val client: HttpClient,
    private val baseUrl: String,
    private val key: String
) : UnsplashApi {
    private val log = LoggerFactory.getLogger(this::javaClass.get())

    override suspend fun listImages(count: Int): List<UnsplashImage> {
        log.info("Listing images from unsplash")
        return client.get(appendPostfix("$baseUrl/photos?per_page=$count"))
    }

    override suspend fun searchImages(count: Int, query: String): UnsplashSearchResponse {
        log.info("Searching images from unsplash")
        return client.get(appendPostfix("$baseUrl/search/photos?query=${urlEncode(query)}&per_page=$count"))
    }

    override suspend fun getRandomImage(): UnsplashImage {
        log.info("Retrieving random image from unsplash")
        return client.get(appendPostfix("$baseUrl/photos/random"))
    }

    private fun appendPostfix(request: String) =
        "$request&orientation=portrait&content_filter=high&order_by=latest&client_id=$key"

    private fun urlEncode(str: String) = URLEncoder.encode(str, UTF_8.name())
}