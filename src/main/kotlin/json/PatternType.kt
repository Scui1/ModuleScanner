package json

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
enum class PatternType {
    @SerialName("Function") FUNCTION,
    @SerialName("ReturnAddress") RETURN_ADDRESS,
    @SerialName("Offset") OFFSET,
    @SerialName("Index") INDEX
}