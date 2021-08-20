package app.wallfact.plugins

import app.wallfact.integration.pixabay.model.PixabayResponse
import app.wallfact.integration.pixabay.repo.PixabayRepo
import app.wallfact.integration.pixabay.service.PixabayService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val pixabayService: PixabayService by inject()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/img") {
            try {
                val img = pixabayService.forceWriteToMongo()
                call.respond("")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}