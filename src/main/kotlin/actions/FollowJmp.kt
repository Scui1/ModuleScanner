package actions

import pefile.PEFile

object FollowJmp : ExecutableAction {
    override val name = "FollowJmp"

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int {
        val jmpInstruction = peFile.bytes[currentOffset].toUByte()
        val isShortJmp = jmpInstruction == 0xEB.toUByte()

        // relative address is only 1 byte if it's a short jmp
        val relAddress = if (isShortJmp)
            peFile.bytes[currentOffset + 1].toInt()
        else
            peFile.readInt(currentOffset + 1)

        val instructionSize = if (isShortJmp) 2 else 5

        return currentOffset + relAddress + instructionSize
    }
}