import actions.ActionManager
import pefile.PEFile

class ModuleProcessor(private val moduleBytes: ByteArray, var moduleConfig: json.config.Module) {

    fun process() {
        val peFile = PEFile(moduleBytes)
        if (!peFile.isValid()) {
            println("Module ${moduleConfig.name} is not a valid pe file, module won't be processed")
            return
        }

        for (pattern in moduleConfig.patterns) {

            var currentAddress = 0
            for (action in pattern.actions) {
                currentAddress = ActionManager.executeAction(action, peFile, currentAddress)
                if (currentAddress == 0) {
                    println("Failed to find pattern for ${pattern.name} because ${action.type} failed.")
                    break
                }
            }

            if (currentAddress == 0)
                continue

            val patternResult = peFile.convertRawOffsetToVirtualOffset(currentAddress, ".text")
            println("Offset for ${pattern.name} found: 0x${patternResult.toString(16)}")
        }
    }

}