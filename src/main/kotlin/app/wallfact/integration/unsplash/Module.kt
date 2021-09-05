package app.wallfact.integration.unsplash

import app.wallfact.fact.FactService
import app.wallfact.integration.unsplash.client.UnsplashClient
import app.wallfact.integration.unsplash.repo.UnsplashRepo
import app.wallfact.integration.unsplash.service.UnsplashService
import app.wallfact.job.unsplash.UnsplashDailyImageFetcherJob
import app.wallfact.service.ImageService
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

val unsplashModule = module {
    single {
        UnsplashClient(client, prop("integration.unsplash.baseUrl"), System.getenv("UNSPLASH_KEY"))
    }

    single {
        val unsplashClient: UnsplashClient by inject()
        UnsplashRepo(unsplashClient)
    }

    single {
        val client = KMongo.createClient(System.getenv("MONGODB_URI")).coroutine
        client.getDatabase(prop("database.name"))
    }

    single {
        val unsplashRepo: UnsplashRepo by inject()
        val database: CoroutineDatabase by inject()
        UnsplashService(unsplashRepo, database)
    }

    single {
        val database: CoroutineDatabase by inject()
        FactService(database)
    }

    single {
        val unsplashService: UnsplashService by inject()
        val factService: FactService by inject()
        val database: CoroutineDatabase by inject()

        ImageService(unsplashService, factService, database)
    }

    single(createdAtStart = true) {
        val unsplashService: UnsplashService by inject()
        val database: CoroutineDatabase by inject()
        UnsplashDailyImageFetcherJob(unsplashService, database)
    }
}

fun prop(property: String): String = config.property(property).getString()