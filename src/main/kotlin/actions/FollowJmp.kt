package actions

import pefile.PEFile

object FollowJmp : ExecutableAction {
    override val name = "FollowJmp"

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int {
        val currentInstruction = peFile.bytes[currentOffset].toUByte()

        val isShortJmp = when(currentInstruction) {
            0xEB.toUByte() -> true
            0xE8.toUByte() -> false
            else -> throw ActionException("Instruction 0x${currentInstruction.toString(16)} is not a jmp.")
        }

        // relative address is only 1 byte if it's a short jmp
        val relAddress = if (isShortJmp)
            peFile.bytes[currentOffset + 1].toInt()
        else
            peFile.readInt(currentOffset + 1)

        val instructionSize = if (isShortJmp) 2 else 5

        val result = currentOffset + relAddress + instructionSize
        return when {
            result < peFile.bytes.size && result >= 0 -> result
            else -> throw ActionException("Jmp redirects outside module bounds. Perhaps it isn't a jmp instruction?")
        }
    }
}