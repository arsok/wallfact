package app.wallfact.integration.unsplash.service

import app.wallfact.integration.unsplash.model.UnsplashImage
import app.wallfact.integration.unsplash.repo.UnsplashRepo
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import kotlin.random.Random
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.BsonDocument
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.LoggerFactory

class UnsplashService(
    private val unsplashRepo: UnsplashRepo,
    private val database: CoroutineDatabase
) {
    private val imagesTheme = "nature"
    private val log = LoggerFactory.getLogger(this::javaClass.get())

    suspend fun getNewImages(count: Int): List<UnsplashImage> {
        return unsplashRepo.searchImages(count, imagesTheme).results
    }

    suspend fun getRandomCroppedImageFromDb(width: Int? = null, height: Int? = null): ByteArray {
        log.info("Retrieving random image from database")
        val collection = database.getCollection<BsonDocument>("images")
        val total = collection.collection.countDocuments().awaitFirst().toInt()

        return collection.find()
            .limit(-1)
            .skip(Random.nextInt(1, total))
            .first()
            ?.getBinary("image")
            ?.data
            ?.let { cropIfNeeded(it, width, height) }
            ?: byteArrayOf()
    }

    suspend fun clearImages() {
        val collection = database.getCollection<BsonDocument>("images")
        collection.deleteMany()
    }

    private fun cropIfNeeded(byteArray: ByteArray, width: Int?, height: Int?): ByteArray {
        if (width == null || height == null || width < 240 || height < 240) {
            return byteArray
        }

        var imgToCrop = ImageIO.read(byteArray.inputStream())
        if (imgToCrop.width < width || imgToCrop.height < height) {
            return byteArray
        }

        imgToCrop = imgToCrop.getSubimage(0, 0, width, height)

        return ByteArrayOutputStream().apply { ImageIO.write(imgToCrop, "png", this) }.toByteArray()
    }
}