package app.wallfact.job.pixabay

import app.wallfact.integration.pixabay.service.PixabayService
import app.wallfact.job.DailyFetcherJob
import java.net.URL
import kotlin.concurrent.fixedRateTimer
import kotlinx.coroutines.runBlocking
import org.bson.BsonBinary
import org.bson.BsonDocument
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.LoggerFactory

class PixabayDailyImageFetcherJob(
    private val pixabayService: PixabayService,
    private val database: CoroutineDatabase
) : DailyFetcherJob {
    private val log = LoggerFactory.getLogger(this::javaClass.get())

    init {
        createPixabayFetcher()
    }

    override suspend fun fetch() {
        log.info("Running pixabay import job")
        val (_, _, hits) = pixabayService.getFiftyNewImages()

        val collection = database.getCollection<BsonDocument>("images")
        val bsonImages = hits.map { URL(it.largeImageURL) }
            .map { BsonDocument("image", BsonBinary(it.readBytes())) }

        collection.insertMany(bsonImages)
        log.info("Pixabay import job completed")
    }

    private fun createPixabayFetcher() = fixedRateTimer(period = 86_400_000L, name = "pixabay-job-runner") {
        runBlocking {
            fetch()
        }
    }
}