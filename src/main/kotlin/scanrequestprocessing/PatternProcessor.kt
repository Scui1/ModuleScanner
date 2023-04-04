package scanrequestprocessing

import actions.ActionException
import actions.ActionManager
import json.PatternType
import json.scanrequest.Pattern
import json.scanresult.ScanError
import json.scanresult.ScanResult
import org.slf4j.LoggerFactory
import pefile.PEFile

private val logger = LoggerFactory.getLogger("PatternProcessor")

fun processPattern(peFile: PEFile, pattern: Pattern, output: ScanResult) {
    if (pattern.actions.isEmpty()) {
        output.errors.add(ScanError(pattern.type, pattern.name, "No actions are defined."))
        logger.trace("Failed to find pattern for ${pattern.name} because no actions are defined.")
        return
    }

    var currentResult = 0
    for (i in pattern.actions.indices) {
        val action = pattern.actions[i]
        try {
            currentResult = ActionManager.executeAction(action, peFile, currentResult)
        } catch (exception: ActionException) {
            output.errors.add(ScanError(pattern.type, pattern.name, "Action ${i + 1} (${action.type}) failed. ${exception.message}"))
            logger.trace("Failed to find pattern for ${pattern.name} because ${action.type} failed.")
            return
        }
    }

    if (pattern.type == PatternType.FUNCTION || pattern.type == PatternType.RETURN_ADDRESS) {
        currentResult = peFile.convertRawOffsetToVirtualOffset(currentResult, ".text")
        logger.trace("${pattern.type} ${pattern.name} found: 0x${currentResult.toString(16)}")
    } else {
        logger.trace("${pattern.type} ${pattern.name} found: $currentResult")
    }

    when (pattern.type) {
        PatternType.FUNCTION -> output.getFunctionsForModule(peFile.name)[pattern.name] = currentResult
        PatternType.RETURN_ADDRESS -> output.getReturnAddressesForModule(peFile.name)[pattern.name] = currentResult
        PatternType.INDEX -> output.vfunc[pattern.name] = currentResult
        PatternType.OFFSET -> output.offset[pattern.name] = currentResult
    }
}