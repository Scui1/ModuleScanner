package web

import disassemblerequestprocessing.disassembleScanResult
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

            call.respond(HttpStatusCode.OK, result)
        }

        post("/disassemble") {
            val config = call.receive<ScanRequest>()

            val scanResult = processScanRequest(config)

            disassembleScanResult(scanResult)
                .onSuccess { message ->
                    call.respond(HttpStatusCode.OK, message)
                }.onFailure { error ->
                    call.respond(HttpStatusCode.BadRequest, error.message ?: "Unknown message lol")
                }
        }

        get("/alive") {
            call.respond(HttpStatusCode.OK, true)
        }
    }
}