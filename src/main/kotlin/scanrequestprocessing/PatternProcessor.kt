package scanrequestprocessing

import actions.ActionException
import actions.ActionManager
import actions.Deref
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

    var currentResult = 0
    for (i in pattern.actions.indices) {
        val action = pattern.actions[i]
        try {
            currentResult = ActionManager.executeAction(action, peFile, currentResult)
        } catch (exception: ActionException) {
            return PatternResult.Error("Action ${i + 1} (${action.type}) failed. ${exception.message}")
        }
    }

    // TODO: This is ghetto atm, introduce some custom result type that specifies if result is virtual or raw, so we can get rid of this
    if ((pattern.type == PatternType.FUNCTION || pattern.type == PatternType.ADDRESS) && pattern.actions.last().type != Deref.name) {
        currentResult = peFile.convertRawOffsetToVirtualOffset(currentResult)
        logger.trace("${pattern.type} ${pattern.name} found: 0x${currentResult.toString(16)}")
    } else {
        logger.trace("${pattern.type} ${pattern.name} found: $currentResult")
    }

    return PatternResult.Success(currentResult)
}