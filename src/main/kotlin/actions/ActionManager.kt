package actions

import json.config.Action
import pefile.PEFile

object ActionManager {
    private val actions = listOf(PatternSearch, StringSearch, Offset)

    fun executeAction(action: Action, peFile: PEFile, currentOffset: Int): Int {
        val executableAction = findActionByName(action.type)
        if (executableAction == null) {
            println("Action ${action.type} doesn't exist. Please check your spelling")
            return 0
        }

        return executableAction.execute(peFile, currentOffset, action.arguments)
    }


    private fun findActionByName(searchedName: String): ExecutableAction? {
        return actions.find { it.name.equals(searchedName, true) }
    }
}