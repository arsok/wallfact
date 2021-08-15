package app.wallfact

import app.wallfact.integration.pixabay.impl.PixabayRepo
import app.wallfact.model.Fact
import app.wallfact.plugins.configureRouting
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main() {
    runBlocking {
        val response = PixabayRepo().testKtorClient()
        println(response.content.readUTF8Line(Int.MAX_VALUE))
    }

    val client = KMongo.createClient(System.getenv("MONGODB_URI")).coroutine
    val database = client.getDatabase("wallfactdb")
    val col = database.getCollection<Fact>()

    runBlocking {
        val facts: List<Fact> = col.find().toList()
        println(facts)
    }

    embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
        configureRouting()
    }.start(wait = true)
}
