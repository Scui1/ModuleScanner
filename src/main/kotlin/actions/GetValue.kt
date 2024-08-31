package actions

import pefile.PEFile

object GetValue : ExecutableAction {
    override val name = "GetValue"

    private object Parameters {
        const val SIZE = 0
    }

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): ActionResult {
        val sizeToRead = if (arguments.isNotEmpty()) arguments[Parameters.SIZE].toIntOrNull()?: 4 else 4

        return ActionResult(peFile.readIntWithSize(currentOffset, sizeToRead))
    }
}