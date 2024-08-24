package actions

import pefile.PEFile

object Deref : ExecutableAction {
    override val name = "Deref"

    private object Parameters {
        const val TIMES_TO_DEREF = 0
    }

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int {
        val timesToDeref = if (arguments.isNotEmpty()) arguments[Parameters.TIMES_TO_DEREF].toIntOrNull()?: 1 else 1

        if (peFile.architecture.is32Bit()) {
            val imageBase = peFile.getImageBase()

            var currentAddress = currentOffset
            for (i in 0 until timesToDeref) {
                currentAddress = peFile.readInt(currentAddress)
                if (currentAddress < imageBase) // TODO: do we need a check if the address lies outside of the module?
                    throw ActionException("Encountered error at deref ${i + 1}. Cannot deref value 0x${currentAddress.toString(16)}.")
                else
                    currentAddress -= imageBase
                currentAddress = peFile.convertVirtualOffsetToRawOffset(currentAddress)
            }

            return currentAddress
        } else {
            // TODO: Implement multiple derefs and deref relative or absolute
            val relativeVirtualAddress = peFile.readInt(currentOffset)
            val currentVirtualAddressPlus4 = peFile.convertRawOffsetToVirtualOffset(currentOffset + 4)
            return currentVirtualAddressPlus4 + relativeVirtualAddress
        }

    }

}