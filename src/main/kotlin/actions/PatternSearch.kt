package actions

import patternsearching.PatternByte
import patternsearching.PatternSearcher
import pefile.PEFile

object PatternSearch : ExecutableAction {
    override val name: String = "PatternSearch"

    private object Parameters {
        const val PATTERN = 0
        const val OCCURRENCES = 1
    }

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int {
        val pattern = arguments[Parameters.PATTERN]
        val wantedOccurrences = if (arguments.size > 1) arguments[Parameters.OCCURRENCES].toInt() else 1

        val patternBytes = mutableListOf<PatternByte>()
        pattern.split(" ").forEach {
            if (it == "?" || it == "??")
                patternBytes.add(PatternByte(0u, true))
            else
                patternBytes.add(PatternByte(it.toUByte(16), false))
        }

        val textSection = peFile.getSectionByName(".text")
        if (textSection == null) {
            println("Failed to find .text section, this shouldn't happen")
            return 0
        }

        return PatternSearcher.searchPattern(peFile, textSection, patternBytes, wantedOccurrences)
    }
}