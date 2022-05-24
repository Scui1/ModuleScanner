package actions

import pefile.PEFile

object GetVFuncIndex : ExecutableAction {
    override val name = "GetVFuncIndex"

    private object Parameters {
        const val SIZE = 0
    }

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int {
        val sizeToRead = if (arguments.isNotEmpty()) arguments[Parameters.SIZE].toIntOrNull()?: 4 else 4

        val intValue = peFile.readIntWithSize(currentOffset, sizeToRead)

        return when {
            intValue.mod(4) == 0 -> intValue / 4
            else -> ActionResultType.ERROR // we assume if the value isn't divideable by 4, the sig is wrong
        }
    }
}