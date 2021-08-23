package app.wallfact.integration.unsplash.service

import app.wallfact.integration.unsplash.model.UnsplashImage
import app.wallfact.integration.unsplash.repo.UnsplashRepo
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

    suspend fun getRandomImageFromDb(): ByteArray {
        log.info("Retrieving random image from database")
        val collection = database.getCollection<BsonDocument>("images")
        val total = collection.collection.countDocuments().awaitFirst().toInt()

        return collection.find()
            .limit(-1)
            .skip(Random.nextInt(total))
            .first()
            ?.getBinary("image")
            ?.data
            ?: byteArrayOf()
    }

    suspend fun clearImages() {
        val collection = database.getCollection<BsonDocument>("images")
        collection.deleteMany()
    }
}