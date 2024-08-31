package actions

import pefile.PEFile

object Offset : ExecutableAction {
    override val name: String = "Offset"

    private object Parameters {
        const val OFFSET = 0
    }

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): ActionResult {
        val offsetValue = arguments[Parameters.OFFSET].toIntOrNull()

        return when(offsetValue) {
            null -> throw ActionException("No offset value was provided.")
            else -> ActionResult(currentOffset + offsetValue)
        }
    }
}