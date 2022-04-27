import actions.PatternSearch
import pefile.PEFile

class ModuleProcessor(private val moduleBytes: ByteArray, var moduleConfig: json.config.Module) {

    fun process() {
        val peFile = PEFile(moduleBytes)
        if (!peFile.isValid()) {
            println("Module ${moduleConfig.name} is not a valid pe file, module won't be processed")
            return
        }

        for (pattern in moduleConfig.patterns) {
            println("Processing pattern: ${pattern.name}")

            for (action in pattern.actions) {
                if (action.type == "PatternSearch") {
                    val obj = PatternSearch()
                    val execResult = obj.execute(peFile, action.arguments)
                    println("Execute returned: $execResult")
                }
                /*when (action.type) {
                    "PatternSearch" ->
                }*/
            }
        }
    }

}