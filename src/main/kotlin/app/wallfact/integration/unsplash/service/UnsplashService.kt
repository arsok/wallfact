package app.wallfact.integration.unsplash.service

import app.wallfact.integration.unsplash.repo.UnsplashRepo
import com.mongodb.client.model.Aggregates
import org.bson.BsonDocument
import org.litote.kmongo.coroutine.CoroutineDatabase

private const val imagesTheme = "nature"

class UnsplashService(
    private val unsplashRepo: UnsplashRepo,
    private val database: CoroutineDatabase
) {
    private val randomPipeline = listOf(Aggregates.sample(1))

    suspend fun retrieveFreshImages(count: Int) = unsplashRepo.searchImages(count, imagesTheme).results

    suspend fun getRandomWallpaper(): ByteArray = database.getCollection<BsonDocument>("images")
        .aggregate<BsonDocument>(randomPipeline)
        .first()
        ?.getBinary("image")
        ?.data
        ?: error("Could not get random image from database")
}