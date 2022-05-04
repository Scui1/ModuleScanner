import json.scanrequest.ScanRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun readConfig(): ScanRequest? {
    val configString = object{}.javaClass.getResource("sampleScanRequest.json")?.readText()
    return configString?.let { Json.decodeFromString<ScanRequest>(it) }
}