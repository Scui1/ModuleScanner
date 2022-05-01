package json.output

@kotlinx.serialization.Serializable
data class ScanError(val patternName: String, val description: String)
