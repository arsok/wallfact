package app.wallfact

import app.wallfact.integration.pixabay.pixabayModule
import app.wallfact.plugins.configureRouting
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.core.context.startKoin

fun main() {
    startKoin {
        modules(pixabayModule)
    }

    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: "8080".toInt()) {
        configureRouting()
    }.start(wait = true)
}