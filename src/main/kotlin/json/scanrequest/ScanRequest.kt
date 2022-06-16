package json.scanrequest

@kotlinx.serialization.Serializable
data class ScanRequest(val modules: List<Module>)
