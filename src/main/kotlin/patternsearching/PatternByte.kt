package patternsearching

data class PatternByte(val value: UByte, val isWildcard: Boolean = false) {
    fun matches(checkByte: UByte): Boolean = isWildcard || value == checkByte
}
