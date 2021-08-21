package app.wallfact.integration.pixabay

import app.wallfact.integration.pixabay.client.PixabayClient
import app.wallfact.integration.pixabay.repo.PixabayRepo
import app.wallfact.integration.pixabay.service.PixabayService
import app.wallfact.job.pixabay.PixabayDailyImageFetcherJob
import com.typesafe.config.ConfigFactory
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.config.HoconApplicationConfig
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

private val config = HoconApplicationConfig(ConfigFactory.load())
private val client = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(Json {
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

val pixabayModule = module {
    single {
        PixabayClient(client, "${prop("integration.pixabay.baseUrl")}?key=${prop("integration.pixabay.key")}")
    }

    single {
        val pixabayClient: PixabayClient by inject()
        PixabayRepo(pixabayClient)
    }

    single {
        val client = KMongo.createClient().coroutine
        client.getDatabase(prop("database.name"))
    }

    single {
        val pixabayRepo: PixabayRepo by inject()
        val database: CoroutineDatabase by inject()
        PixabayService(pixabayRepo, database)
    }

    single(createdAtStart = true) {
        val pixabayService: PixabayService by inject()
        val database: CoroutineDatabase by inject()
        PixabayDailyImageFetcherJob(pixabayService, database)
    }
}

fun prop(property: String): String = config.property(property).getString()