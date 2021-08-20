package app.wallfact

import app.wallfact.integration.pixabay.pixabayModule
import app.wallfact.plugins.configureRouting
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(pixabayModule)
    }

    val client = KMongo.createClient().coroutine
    val database = client.getDatabase("wallfactdb")
    val col = database.getCollection<Fact>()

    runBlocking {
        val facts: List<Fact> = col.find().toList()
        println(facts)
    }

    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: "8080".toInt()) {
        configureRouting()
    }.start(wait = true)
}