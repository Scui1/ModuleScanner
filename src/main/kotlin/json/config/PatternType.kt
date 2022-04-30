package json.config

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
enum class PatternType {
    @SerialName("Address") ADDRESS,
    @SerialName("Offset") OFFSET,
    @SerialName("Index") INDEX
}