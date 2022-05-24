package actions

import pefile.PEFile

object Offset : ExecutableAction {
    override val name: String = "Offset"

    private object Parameters {
        const val OFFSET = 0
    }

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int {
        val offsetValue = arguments[Parameters.OFFSET].toIntOrNull()

        return when(offsetValue) {
            null -> ActionResultType.ERROR
            else -> currentOffset + offsetValue
        }
    }
}