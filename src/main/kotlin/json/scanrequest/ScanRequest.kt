package json.scanrequest

@kotlinx.serialization.Serializable
data class ScanRequest(val modulePath: String, val modules: List<Module>)
