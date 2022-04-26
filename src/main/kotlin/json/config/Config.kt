package json.config

@kotlinx.serialization.Serializable
data class Config(val modulePath: String, val modules: List<Module>)
