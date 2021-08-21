package app.wallfact.plugins

import app.wallfact.integration.pixabay.service.PixabayService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondBytes
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val pixabayService: PixabayService by inject()

    routing {
        get("/") {
            call.respondText("Welcome to WallFact")
        }

        get("/img") {
            val image = pixabayService.getRandomImageFromDb()
            call.respondBytes(image)
        }
    }
}