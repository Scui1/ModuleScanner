import actions.ActionManager
import actions.ActionResultType
import json.scanrequest.PatternType
import json.scanresult.ScanError
import pefile.PEFile

fun processModule(peFile: PEFile, moduleConfig: json.scanrequest.Module, output: json.scanresult.ScanResult) {
    for (pattern in moduleConfig.patterns) {

        var currentResult = 0
        for (action in pattern.actions) {
            currentResult = ActionManager.executeAction(action, peFile, currentResult)
            if (currentResult == ActionResultType.ERROR) {
                output.errors.add(ScanError(pattern.name, "${action.type} failed"))
                println("Failed to find pattern for ${pattern.name} because ${action.type} failed.")
                break
            }
        }

        if (currentResult == ActionResultType.ERROR)
            continue

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