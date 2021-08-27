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
import java.awt.Dimension
import kotlin.time.ExperimentalTime
import org.koin.ktor.ext.inject

@ExperimentalTime
fun Application.configureRouting() {
    val unsplashService: UnsplashService by inject()

    routing {
        get("/") {
            call.respondText("Welcome to WallFact")
        }

        get("/img") {
            with(call.request.queryParameters) {
                val dimension = Dimension(this["width"]?.toInt() ?: 0, this["height"]?.toInt() ?: 0)
                val image = unsplashService.getRandomCroppedImageFromDb(dimension)

                call.respondBytes(image)
            }
        }

        get("/clear") {
            unsplashService.clearImages()
            call.respond(HttpStatusCode.OK)
        }
    }
}