package app.wallfact

import app.wallfact.integration.unsplash.unsplashModule
import app.wallfact.plugins.configureRouting
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlin.time.ExperimentalTime
import org.koin.core.context.startKoin

@ExperimentalTime
fun main() {
    startKoin {
        modules(unsplashModule)
    }

    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: "8080".toInt()) {
        configureRouting()
    }.start(wait = true)
}