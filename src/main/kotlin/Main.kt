import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun main() {
    val config = readConfig()?: return

    val result = processScanRequest(config)

    val json = Json { prettyPrint = true }
    println(json.encodeToString(result))
}