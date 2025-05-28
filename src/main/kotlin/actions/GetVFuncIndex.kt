package actions

import pefile.PEFile

object GetVFuncIndex : ExecutableAction {
    override val name = "GetVFuncIndex"

    private object Parameters {
        const val SIZE = 0
    }

    override fun execute(peFile: PEFile, currentResult: ActionResult, arguments: List<String>): ActionResult {
        val sizeToRead = if (arguments.isNotEmpty()) arguments[Parameters.SIZE].toIntOrNull()?: 4 else 4

        val intValue = peFile.readIntWithSize(currentResult.value, sizeToRead)
        val vtableEntrySize = peFile.architecture.getVTableEntrySize()

        return when {
            intValue.mod(vtableEntrySize) == 0 -> ActionResult(intValue / vtableEntrySize)
            else -> throw ActionException("Value '$intValue' is not a vfunc index.") // we assume if the value isn't divideable by 4, the sig is wrong
        }
    }
}