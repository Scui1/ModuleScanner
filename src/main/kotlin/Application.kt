import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import scanrequestprocessing.ModuleReader
import web.configureRouting

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.main() {
    install(ContentNegotiation) {
        json()
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            cause.printStackTrace()
            call.respondText(text = cause.stackTraceToString(), status = HttpStatusCode.InternalServerError)
        }
    }
    install(CORS) {
        allowSameOrigin = true
        anyHost()
        allowHeader(HttpHeaders.ContentType)
    }

    configureRouting()

    ModuleReader.moduleDirectory = environment.config.property("moduleDirectory").getString()
}