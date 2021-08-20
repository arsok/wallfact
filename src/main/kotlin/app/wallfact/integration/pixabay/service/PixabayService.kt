package app.wallfact.integration.pixabay.service

import app.wallfact.integration.pixabay.repo.PixabayRepo
import kotlin.concurrent.fixedRateTimer
import kotlinx.coroutines.runBlocking

class PixabayService(private val pixabayRepo: PixabayRepo) {

    init {
        createPixabayFetcher()
    }

    private fun createPixabayFetcher() {
        fixedRateTimer(period = 86_400_000L, name = "Pixabay-0") {
            runBlocking {
                val (_, _, hits) = pixabayRepo.getImages(50, "new")
                var imageUrls = hits.map { it.largeImageURL }
            }
        }
    }

    fun forceWriteToMongo() {
        runBlocking {
            val (_, _, hits) = pixabayRepo.getImages(50, "new")
            var imageUrls = hits.map { it.largeImageURL }


        }
    }
}