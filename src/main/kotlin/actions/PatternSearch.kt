package actions

import pefile.PEFile

class PatternSearch : ExecutableAction {
    override val name: String = "PatternSearch"
    override fun execute(peFile: PEFile, arguments: List<String>): Int {
        val pattern = arguments[0]
        val wantedOccurrences = if (arguments.size > 1) arguments[1].toInt() else 1

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
        // fix difference between virtual and raw address, as we need the offset to the pattern in memory
        val virtualRawDifference = textSection.virtualBase - textSection.rawBase

        var occurrences = 0
        var foundAddress = 0
        var bytesMatched = 0
        for (i in textSection.rawBase until textSection.rawBase + textSection.size) {
            val currentByte = peFile.bytes[i]
            if (patternBytes[bytesMatched].isWildcard || currentByte.toUByte() == patternBytes[bytesMatched].value) {
                if (bytesMatched == 0)
                    foundAddress = i + virtualRawDifference

                ++bytesMatched
            } else {
                bytesMatched = 0
                foundAddress = 0
            }

            if (bytesMatched >= patternBytes.size)
                ++occurrences

            if (occurrences >= wantedOccurrences)
                return foundAddress
        }

        return 0
    }

    data class PatternByte(val value: UByte, val isWildcard: Boolean)
}