import actions.ActionManager
import actions.ActionResultType
import json.config.PatternType
import pefile.PEFile

class ModuleProcessor(private val moduleBytes: ByteArray, var moduleConfig: json.config.Module) {

    fun process() {
        val peFile = PEFile(moduleBytes)
        if (!peFile.isValid()) {
            println("Module ${moduleConfig.name} is not a valid pe file, module won't be processed")
            return
        }

        for (pattern in moduleConfig.patterns) {

            var currentResult = 0
            for (action in pattern.actions) {
                currentResult = ActionManager.executeAction(action, peFile, currentResult)
                if (currentResult == ActionResultType.ERROR) {
                    println("Failed to find pattern for ${pattern.name} because ${action.type} failed.")
                    break
                }
            }

            if (currentResult == ActionResultType.ERROR)
                continue

            if (pattern.type == PatternType.ADDRESS) {
                val patternResult = peFile.convertRawOffsetToVirtualOffset(currentResult, ".text")
                println("Offset for ${pattern.name} found: 0x${patternResult.toString(16)}")
            }

            // TODO: Exporting result
        }
    }

}