package json.scanresult

@kotlinx.serialization.Serializable
data class ScanResult(val function: MutableMap<String, Int>, val returnaddress: MutableMap<String, Int>, val offset: MutableMap<String, Int>, val vfunc: MutableMap<String, Int>, val errors: MutableList<ScanError>) {
    constructor() : this(mutableMapOf(), mutableMapOf(), mutableMapOf(), mutableMapOf(), mutableListOf())
}

