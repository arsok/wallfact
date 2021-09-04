package app.wallfact.plugins

import app.wallfact.service.ImageService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respondBytes
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import java.awt.Dimension
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val imageService: ImageService by inject()

    routing {
        get("/") {
            call.respondText("Welcome to WallFact")
        }

        get("/img") {
            with(call.request.queryParameters) {
                val dimension = Dimension(this["width"]?.toInt() ?: 0, this["height"]?.toInt() ?: 0)
                val wallpaper = imageService.getWallpaper(dimension)

                call.respondBytes(wallpaper)
            }
        }
    }
}