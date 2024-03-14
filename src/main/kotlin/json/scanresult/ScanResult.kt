package json.scanresult

@kotlinx.serialization.Serializable
data class ScanResult(val function: MutableMap<String, MutableMap<String, Int>>,
                      val address: MutableMap<String, MutableMap<String, Int>>,
                      val offset: MutableMap<String, Int>,
                      val vfunc: MutableMap<String, Int>,
                      val errors: MutableList<ScanError>) {
    constructor() : this(mutableMapOf(), mutableMapOf(), mutableMapOf(), mutableMapOf(), mutableListOf())
    fun getFunctionsForModule(name: String): MutableMap<String, Int> {
        return function.getOrPut(name) { mutableMapOf() }
    }

    fun getAddressesForModule(name: String): MutableMap<String, Int> {
        return address.getOrPut(name) { mutableMapOf() }
    }
}

