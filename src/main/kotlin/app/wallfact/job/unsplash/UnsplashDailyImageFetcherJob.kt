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

class UnsplashDailyImageFetcherJob(
    private val unsplashService: UnsplashService,
    private val database: CoroutineDatabase
) : DailyFetcherJob {
    private val removePreviousImages = true

    private val log = LoggerFactory.getLogger(this::javaClass.get())

    init {
        createUnsplashFetcher()
    }

    override suspend fun fetch() {
        log.info("Running unsplash import job")
        val images = unsplashService.getNewImages(10)

        if (images.isNotEmpty() && removePreviousImages) {
            log.info("Clearing existing images collection")
            unsplashService.clearImages()
        }

        val collection = database.getCollection<BsonDocument>("images")
        images.map { BsonDocument("image", BsonBinary(URL(it.urls.regular).readBytes())) }
            .onEach { collection.insertOne(it) }

        log.info("Unsplash import job completed")
    }

    private fun createUnsplashFetcher() = fixedRateTimer(period = 86_400_000L, name = "unsplash-job-runner") {
        runBlocking {
            fetch()
        }
    }
}