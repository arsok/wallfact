package app.wallfact.integration.pixabay.client

import app.wallfact.integration.pixabay.model.PixabayResponse
import app.wallfact.integration.pixabay.repo.PixabayApi
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import org.slf4j.LoggerFactory

class PixabayClient(private val client: HttpClient, private val baseUrl: String) : PixabayApi {
    private val log = LoggerFactory.getLogger(this::javaClass.get())

    override suspend fun getImages(count: Int, query: String): PixabayResponse {
        log.info("Retrieving images from pixabay")
        return client.get("$baseUrl&q=${urlEncode(query)}&count=$count&image_type=photo")
    }

    override suspend fun getImage(query: String): PixabayResponse {
        log.info("Retrieving image from pixabay")
        return client.get("$baseUrl&q=${urlEncode(query)}&image_type=photo")
    }

    override suspend fun getRandomImage(): PixabayResponse {
        log.info("Retrieving random image from pixabay")
        return client.get("$baseUrl&image_type=photo")
    }

    private fun urlEncode(str: String) = URLEncoder.encode(str, UTF_8.name())
}