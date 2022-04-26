package json.config

import kotlinx.serialization.Serializable

@Serializable
data class Action(val type: String, val arguments: List<String>)
