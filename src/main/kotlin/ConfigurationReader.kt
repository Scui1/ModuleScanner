import json.config.Config
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

fun readConfig(): Config? {
    val configString = object{}.javaClass.getResource("sampleConfig.json")?.readText()
    return configString?.let { Json.decodeFromString<Config>(it) }
}