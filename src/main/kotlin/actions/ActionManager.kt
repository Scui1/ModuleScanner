package actions

import json.scanrequest.Action
import pefile.PEFile

object ActionManager {
    private val actions = listOf(PatternSearch, StringSearch, Offset, FollowJmp, GetExport, GetValue, GetVFuncIndex, Deref)

    @kotlin.jvm.Throws(ActionException::class)
    fun executeAction(action: Action, peFile: PEFile, currentResult: ActionResult): ActionResult {
        val executableAction = findActionByName(action.type)
            ?: throw ActionException("Action '${action.type}' doesn't exist. Please check your spelling")

        return executableAction.execute(peFile, currentResult, action.arguments)
    }


    private fun findActionByName(searchedName: String): ExecutableAction? {
        return actions.find { it.name.equals(searchedName, true) }
    }
}