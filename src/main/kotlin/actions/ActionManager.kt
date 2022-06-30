package actions

import json.scanrequest.Action
import pefile.PEFile

object ActionManager {
    private val actions = listOf(PatternSearch, StringSearch, Offset, FollowJmp, GetValue, GetVFuncIndex, Deref)

    @kotlin.jvm.Throws(ActionException::class)
    fun executeAction(action: Action, peFile: PEFile, currentOffset: Int): Int {
        val executableAction = findActionByName(action.type)
            ?: throw ActionException("Action '${action.type}' doesn't exist. Please check your spelling")

        return executableAction.execute(peFile, currentOffset, action.arguments)
    }


    private fun findActionByName(searchedName: String): ExecutableAction? {
        return actions.find { it.name.equals(searchedName, true) }
    }
}