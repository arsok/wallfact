package app.wallfact.integration.unsplash.service

import app.wallfact.integration.unsplash.model.UnsplashImage
import app.wallfact.integration.unsplash.repo.UnsplashRepo
import io.github.reactivecircus.cache4k.Cache
import java.awt.Dimension
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.random.Random
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.BsonDocument
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.LoggerFactory

@ExperimentalTime
class UnsplashService(
    private val unsplashRepo: UnsplashRepo,
    private val database: CoroutineDatabase
) {
    private val imagesTheme = "nature"
    private val log = LoggerFactory.getLogger(this::javaClass.get())

    private val cache = Cache.Builder()
        .expireAfterAccess((hours(24)))
        .build<String, ByteArray>()

    suspend fun getNewImages(count: Int): List<UnsplashImage> {
        return unsplashRepo.searchImages(count, imagesTheme).results
    }

    suspend fun getRandomCroppedImageFromDb(dimension: Dimension): ByteArray {
        log.info("Retrieving random image from database")
        val collection = database.getCollection<BsonDocument>("images")
        val total = collection.collection.countDocuments().awaitFirst().toInt()

        return collection.find()
            .limit(-1)
            .skip(Random.nextInt(1, total))
            .first()
            ?.getBinary("image")
            ?.data
            ?.let { getImage(it, dimension) }
            ?: byteArrayOf()
    }

    suspend fun clearImages() {
        val collection = database.getCollection<BsonDocument>("images")
        collection.deleteMany()
    }

    private suspend fun getImage(byteArray: ByteArray, dimension: Dimension): ByteArray {
        with(dimension) {
            return if (width == 0 || height == 0 || width < 240 || height < 240) byteArray
            else cache.get("$this${byteArray.size}") { crop(byteArray, this) }
        }
    }

    private fun crop(byteArray: ByteArray, dimension: Dimension): ByteArray {
        var imgToCrop = ImageIO.read(byteArray.inputStream())
        if (imgToCrop.width < dimension.width || imgToCrop.height < dimension.height) {
            return byteArray
        }

        imgToCrop = imgToCrop.getSubimage(0, 0, dimension.width, dimension.height)

        return ByteArrayOutputStream().apply { ImageIO.write(imgToCrop, "png", this) }.toByteArray()
    }
}