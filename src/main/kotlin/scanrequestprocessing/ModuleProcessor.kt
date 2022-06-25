import actions.ActionException
import actions.ActionManager
import json.PatternType
import json.scanresult.ScanError
import pefile.PEFile

fun processModule(peFile: PEFile, moduleConfig: json.scanrequest.Module, output: json.scanresult.ScanResult) {
    patternLoop@ for (pattern in moduleConfig.patterns) {

        if (pattern.actions.isEmpty()) {
            output.errors.add(ScanError(pattern.type, pattern.name, "No actions are defined."))
            println("Failed to find pattern for ${pattern.name} because no actions are defined.")
            continue
        }

        var currentResult = 0
        for (i in pattern.actions.indices) {
            val action = pattern.actions[i]
            try {
                currentResult = ActionManager.executeAction(action, peFile, currentResult)
            } catch (exception: ActionException) {
                output.errors.add(ScanError(pattern.type, pattern.name, "Action ${i + 1} (${action.type}) failed. ${exception.message}"))
                println("Failed to find pattern for ${pattern.name} because ${action.type} failed.")
                continue@patternLoop
            }
        }

        if (pattern.type == PatternType.FUNCTION || pattern.type == PatternType.RETURN_ADDRESS) {
            currentResult = peFile.convertRawOffsetToVirtualOffset(currentResult, ".text")
            println("${pattern.type} ${pattern.name} found: 0x${currentResult.toString(16)}")
        } else {
            println("${pattern.type} ${pattern.name} found: $currentResult")
        }

        when (pattern.type) {
            PatternType.FUNCTION -> output.function[pattern.name] = currentResult
            PatternType.RETURN_ADDRESS -> output.returnaddress[pattern.name] = currentResult
            PatternType.INDEX -> output.vfunc[pattern.name] = currentResult
            PatternType.OFFSET -> output.offset[pattern.name] = currentResult
        }
    }
}