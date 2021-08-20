package app.wallfact.plugins

import app.wallfact.integration.pixabay.model.PixabayResponse
import app.wallfact.integration.pixabay.repo.PixabayRepo
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val pixabayRepo: PixabayRepo by inject()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/img") {
            try {
                val img = pixabayRepo.getImage("test")
                call.respond<PixabayResponse>(img)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}