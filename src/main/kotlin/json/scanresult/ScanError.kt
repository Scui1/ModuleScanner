package json.scanresult

@kotlinx.serialization.Serializable
data class ScanError(val patternName: String, val description: String)
