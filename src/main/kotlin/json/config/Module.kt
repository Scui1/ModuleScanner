package json.config

@kotlinx.serialization.Serializable
data class Module(val name: String, val patterns: List<Pattern>)
