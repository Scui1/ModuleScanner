package actions

import pefile.PEFile

object GetVFuncIndex : ExecutableAction {
    override val name = "GetVFuncIndex"

    private object Parameters {
        const val SIZE = 0
    }

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int {
        val sizeToRead = if (arguments.isNotEmpty()) arguments[Parameters.SIZE].toInt() else 4

        val intValue = peFile.readIntWithSize(currentOffset, sizeToRead)
        return when (intValue) {
            0 -> 0
            else -> intValue / 4
        }
    }
}