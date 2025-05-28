package actions

import pefile.PEFile

interface ExecutableAction {
    val name: String

    @kotlin.jvm.Throws(ActionException::class)
    fun execute(peFile: PEFile, currentResult: ActionResult, arguments: List<String>): ActionResult
}