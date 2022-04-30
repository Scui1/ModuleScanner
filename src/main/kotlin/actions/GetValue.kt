package actions

import pefile.PEFile

object GetValue : ExecutableAction {
    override val name = "GetValue"

    private object Parameters {
        const val SIZE = 0
    }

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int {
        val sizeToRead = if (arguments.isNotEmpty()) arguments[Parameters.SIZE].toInt() else 4

        return peFile.readIntWithSize(currentOffset, sizeToRead)
    }
}