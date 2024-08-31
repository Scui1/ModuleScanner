package actions

import pefile.PEFile

const val JMP_OPCODE_SIZE = 1

object FollowJmp : ExecutableAction {
    override val name = "FollowJmp"

    // leaving other jump opcodes out on purpose because they are not needed
    private val jmpInstructions = listOf(
        JumpInstruction(0xEB.toUByte(), 1),
        JumpInstruction(0xE8.toUByte(), 4),
        JumpInstruction(0xE9.toUByte(), 4)
    )

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): ActionResult {
        val currentInstruction = peFile.bytes[currentOffset].toUByte()

        val jumpInstruction = jmpInstructions.find { it.opCode == currentInstruction }
            ?: throw ActionException("Instruction 0x${currentInstruction.toString(16)} is not a jmp.")

        val relAddress = peFile.readIntWithSize(currentOffset + JMP_OPCODE_SIZE, jumpInstruction.operandSize)

        val result = currentOffset + relAddress + JMP_OPCODE_SIZE + jumpInstruction.operandSize
        return when {
            result < peFile.bytes.size && result >= 0 -> ActionResult(result)
            else -> throw ActionException("Jmp redirects outside module bounds. Perhaps it isn't a jmp instruction?")
        }
    }
}

data class JumpInstruction(val opCode: UByte, val operandSize: Int) {}