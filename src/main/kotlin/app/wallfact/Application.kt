package app.wallfact

import app.wallfact.integration.pixabay.impl.PixabayRepo
import app.wallfact.plugins.configureRouting
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        val response = PixabayRepo().testKtorClient()
        println(response.content.readUTF8Line(Int.MAX_VALUE))
    }

    embeddedServer(Netty, port = System.getenv("PORT").toInt()) {
        configureRouting()
    }.start(wait = true)
}
