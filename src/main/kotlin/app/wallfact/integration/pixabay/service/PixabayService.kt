package app.wallfact.integration.pixabay.service

import app.wallfact.integration.pixabay.model.PixabayResponse
import app.wallfact.integration.pixabay.repo.PixabayRepo
import kotlin.random.Random
import kotlinx.coroutines.reactive.awaitFirst
import org.bson.BsonDocument
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.LoggerFactory

class PixabayService(
    private val pixabayRepo: PixabayRepo,
    private val database: CoroutineDatabase
) {
    private val log = LoggerFactory.getLogger(this::javaClass.get())

    suspend fun getFiftyNewImages(): PixabayResponse {
        return pixabayRepo.getImages(50, "new")
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
}