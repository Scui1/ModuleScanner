package json.config

@kotlinx.serialization.Serializable
data class Pattern(val name: String, val actions: List<Action>)
