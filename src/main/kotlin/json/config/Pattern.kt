package json.config

@kotlinx.serialization.Serializable
data class Pattern(val name: String, val type: PatternType, val actions: List<Action>)
