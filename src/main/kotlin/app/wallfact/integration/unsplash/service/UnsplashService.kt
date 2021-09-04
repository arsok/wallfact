package app.wallfact.integration.unsplash.service

import app.wallfact.integration.unsplash.repo.UnsplashRepo
import com.mongodb.client.model.Aggregates
import org.bson.BsonDocument
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.LoggerFactory

private const val imagesTheme = "nature"

class UnsplashService(
    private val unsplashRepo: UnsplashRepo,
    private val database: CoroutineDatabase
) {
    private val log = LoggerFactory.getLogger(this::javaClass.get())

    suspend fun retrieveFreshImages(count: Int) = unsplashRepo.searchImages(count, imagesTheme).results

    suspend fun getRandomWallpaper(): ByteArray {
        log.info("Retrieving random image from database")

        val first: BsonDocument = database.getCollection<BsonDocument>("images")
            .aggregate<BsonDocument>(listOf(Aggregates.sample(1)))
            .first()
            ?: throw IllegalStateException("Could not get random image from database")

        log.info("Retrieved random image with id ${first.getObjectId("_id").value}")

        return first.getBinary("image").data
    }
}