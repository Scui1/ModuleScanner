package actions

import patternsearching.PatternByte
import patternsearching.PatternSearcher
import pefile.PEFile

object StringSearch : ExecutableAction {
    override val name = "StringSearch"

    private object Parameters {
        const val STRING = 0
        const val OCCURRENCE = 1
        const val ADD_NULL_TERMINATOR = 2
    }

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int {
        val string = arguments[Parameters.STRING]
        val wantedOccurrences = if (arguments.size > 1) arguments[Parameters.OCCURRENCE].toInt() else 1
        val addNullTerminator = if (arguments.size > 2) arguments[Parameters.ADD_NULL_TERMINATOR].toBooleanStrict() else false

        val rawRdataAddress = findStringInRdata(peFile, string, addNullTerminator)
        if (rawRdataAddress == 0) {
            println("String $string couldn't be found.")
            return 0
        }

        // push instruction uses the virtual address of the string in rdata + image base
        val virtualRdataAddress = peFile.convertRawOffsetToVirtualOffset(rawRdataAddress, ".rdata") + peFile.getImageBase()
        val patternBytes = mutableListOf<PatternByte>(PatternByte(0x68u)) // push instruction
        for (i in 0..3)
            patternBytes.add(PatternByte((virtualRdataAddress shr i * 8).toUByte()))

        val textSection = peFile.getSectionByName(".text")
        if (textSection == null) {
            println("Failed to find .text section, this shouldn't happen")
            return 0
        }

        return PatternSearcher.searchPattern(peFile, textSection, patternBytes, wantedOccurrences)
    }

    private fun findStringInRdata(peFile: PEFile, string: String, addNullTerminator: Boolean): Int {
        val stringBytes = mutableListOf<PatternByte>()
        string.map { PatternByte(it.code.toUByte()) }.toCollection(stringBytes)
        if (addNullTerminator)
            stringBytes.add(PatternByte(0u))

        val rdataSection = peFile.getSectionByName(".rdata")
        if (rdataSection == null) {
            println("Failed to find .rdata section, this shouldn't happen")
            return 0
        }

        return PatternSearcher.searchPattern(peFile, rdataSection, stringBytes, 1)
    }
}