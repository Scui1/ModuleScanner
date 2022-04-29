package actions

import patternsearching.PatternByte
import patternsearching.PatternSearcher
import pefile.PEFile

object PatternSearch : ExecutableAction {
    override val name: String = "PatternSearch"

    private object Parameters {
        const val PATTERN = 0
        const val OCCURRENCES = 1
        const val SEARCH_DIRECTION = 2
        const val MAX_BYTES_TO_SEARCH = 3
    }

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int {
        val pattern = arguments[Parameters.PATTERN]
        val wantedOccurrences = if (arguments.size > 1) arguments[Parameters.OCCURRENCES].toInt() else 1
        val searchDirection = if (arguments.size > 2) arguments[Parameters.SEARCH_DIRECTION].toInt() else PatternSearcher.SearchDirection.DOWN
        val maxBytesToSearch = if (arguments.size > 3) arguments[Parameters.MAX_BYTES_TO_SEARCH].toInt() else 200

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

        // currentOffset == 0 means this is the first action for a pattern. We are scanning the whole text section, not only until maxBytesToSearch
        return if (currentOffset == 0)
            PatternSearcher.searchPattern(peFile, textSection, patternBytes, wantedOccurrences)
        else
            PatternSearcher.searchPattern(peFile, textSection, patternBytes.reversed(), wantedOccurrences, currentOffset, maxBytesToSearch, searchDirection)
    }
}