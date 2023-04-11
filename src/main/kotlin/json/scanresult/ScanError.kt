package json.scanresult

import json.PatternType

@kotlinx.serialization.Serializable
data class ScanError(val moduleName: String, val patternType: PatternType, val patternName: String, val description: String)
