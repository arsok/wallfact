package app.wallfact.plugins

import app.wallfact.integration.unsplash.service.UnsplashService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondBytes
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val unsplashService: UnsplashService by inject()

    routing {
        get("/") {
            call.respondText("Welcome to WallFact")
        }

        get("/img") {
            with(call.request.queryParameters) {
                val (width, height) = this["width"] to this["height"]
                val image = unsplashService.getRandomCroppedImageFromDb(width?.toInt(), height?.toInt())

                call.respondBytes(image)
            }
        }

        get("/clear") {
            unsplashService.clearImages()
            call.respond(HttpStatusCode.OK)
        }
    }
}