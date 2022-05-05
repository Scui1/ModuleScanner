package web

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import json.scanrequest.ScanRequest
import processScanRequest

fun Application.configureRouting() {
    routing {
        post("/executeScan") {
            val config = call.receive<ScanRequest>()

            val result = processScanRequest(config)

            call.response.status(HttpStatusCode.OK)
            call.respond(result)
        }
    }
}