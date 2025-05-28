package actions

import patternsearching.PatternByte
import patternsearching.searchPattern
import pefile.PEFile
import pefile.Section

object StringSearch : ExecutableAction {
    override val name = "StringSearch"

    private object Parameters {
        const val STRING = 0
        const val OCCURRENCE = 1
        const val ADD_NULL_TERMINATOR = 2
    }

    override fun execute(peFile: PEFile, currentResult: ActionResult, arguments: List<String>): ActionResult {
        val string = arguments[Parameters.STRING]
        val wantedOccurrences = if (arguments.size > 1) arguments[Parameters.OCCURRENCE].toIntOrNull()?: 1 else 1
        val addNullTerminator = if (arguments.size > 2) arguments[Parameters.ADD_NULL_TERMINATOR].toBoolean() else false

        val rawRdataAddress = findStringInRdata(peFile, string, addNullTerminator)
        if (rawRdataAddress == 0)
            throw ActionException("String '$string' couldn't be found.")

        val textSection = peFile.getSectionByName(".text")
            ?: throw ActionException("Failed to find .text section, this shouldn't happen")

        val foundAddress = if (peFile.architecture.is32Bit()) {
            // push instruction uses the virtual address of the string in rdata + image base
            val virtualRdataAddress = peFile.convertRawOffsetToVirtualOffset(rawRdataAddress, ".rdata") + peFile.getImageBase()
            val patternBytes = mutableListOf(PatternByte(0x68u)) // push instruction
            for (i in 0..3)
                patternBytes.add(PatternByte((virtualRdataAddress shr i * 8).toUByte()))

            searchPattern(peFile, textSection, patternBytes, wantedOccurrences)
        } else {
            findXrefsForString(peFile, textSection, peFile.convertRawOffsetToVirtualOffset(rawRdataAddress, ".rdata") , wantedOccurrences)
        }

        return when (foundAddress) {
            0 -> throw ActionException("No/not enough xrefs in .text section found for string '$string'.")
            else -> ActionResult(foundAddress)
        }
    }

    private fun findStringInRdata(peFile: PEFile, string: String, addNullTerminator: Boolean): Int {
        val stringBytes = mutableListOf<PatternByte>()
        string.map { PatternByte(it.code.toUByte()) }.toCollection(stringBytes)
        if (addNullTerminator)
            stringBytes.add(PatternByte(0u))

        val rdataSection = peFile.getSectionByName(".rdata")
            ?: throw ActionException("Failed to find .rdata section, this shouldn't happen")

        return searchPattern(peFile, rdataSection, stringBytes, 1)
    }

    private fun findXrefsForString(peFile: PEFile, section: Section, stringAddress: Int, wantedOccurrences: Int): Int {
        val differenceToVirtualAddress = section.virtualBase - section.rawBase

        var occurrences = 0
        // typical instruction we're looking for looks like this: 4? 8D ?? AA BB CC DD
        for (i in section.rawBase until section.rawBase + section.rawSize) {
            val currentByte = peFile.bytes[i].toUByte().toUInt()
            if ((currentByte and 0xF0u) != 0x40u || peFile.bytes[i + 1].toUByte().toUInt() != 0x8Du)
                continue

            val relativeAddress = peFile.readInt(i + 3)
            val resolvedAddress = i + 7 + differenceToVirtualAddress + relativeAddress
            if (resolvedAddress == stringAddress) {
                occurrences++
                if (occurrences == wantedOccurrences)
                    return i // start of instruction
            }
        }

        return 0
    }
}