package scanrequestprocessing

import actions.ActionException
import actions.ActionManager
import actions.ActionResult
import json.PatternType
import json.scanrequest.Pattern
import org.slf4j.LoggerFactory
import pefile.PEFile

private val logger = LoggerFactory.getLogger("PatternProcessor")

sealed class PatternResult {
    data class Success(val value: Int) : PatternResult()
    data class Error(val message: String) : PatternResult()
}

fun processPattern(pattern: Pattern, peFile: PEFile): PatternResult {
    if (pattern.actions.isEmpty()) {
        return PatternResult.Error("No actions are defined.")
    }

    var currentResult = ActionResult()
    for (i in pattern.actions.indices) {
        val action = pattern.actions[i]
        try {
            currentResult = ActionManager.executeAction(action, peFile, currentResult.value)
        } catch (exception: ActionException) {
            return PatternResult.Error("Action ${i + 1} (${action.type}) failed. ${exception.message}")
        }
    }

    val resultShouldBeVirtualAddress = pattern.type == PatternType.FUNCTION || pattern.type == PatternType.ADDRESS
    if (resultShouldBeVirtualAddress) {
        if (!currentResult.isVirtual) {
            currentResult = ActionResult(peFile.convertRawOffsetToVirtualOffset(currentResult.value))
        }
        logResult(pattern, currentResult.value.toString(16))
    } else {
        logResult(pattern, currentResult.value.toString())
    }

    return PatternResult.Success(currentResult.value)
}

private fun logResult(pattern: Pattern, resultAsString: String) {
    logger.trace("${pattern.type} ${pattern.name} found: $resultAsString")
}