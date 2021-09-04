package app.wallfact.job.unsplash

import app.wallfact.integration.unsplash.service.UnsplashService
import app.wallfact.job.DailyFetcherJob
import java.net.URL
import kotlin.concurrent.fixedRateTimer
import kotlinx.coroutines.runBlocking
import org.bson.BsonBinary
import org.bson.BsonDocument
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.slf4j.LoggerFactory

private const val removePreviousImages = true

class UnsplashDailyImageFetcherJob(
    private val unsplashService: UnsplashService,
    private val database: CoroutineDatabase
) : DailyFetcherJob {

    private val log = LoggerFactory.getLogger(this::javaClass.get())

    init {
        createUnsplashFetcher()
    }

    override suspend fun fetch() {
        log.info("Running unsplash import job")
        val collection = database.getCollection<BsonDocument>("images")

        if (removePreviousImages) {
            log.info("Clearing existing images collection")
            collection.deleteMany()
        }

        unsplashService.retrieveFreshImages(10)
            .map { BsonDocument("image", BsonBinary(URL(it.urls.full).readBytes())) }
            .onEach { collection.insertOne(it) }

        log.info("Unsplash import job completed")
    }

    private fun createUnsplashFetcher() = fixedRateTimer(period = 86_400_000L, name = "unsplash-job-runner") {
        runBlocking {
            fetch()
        }
    }
}