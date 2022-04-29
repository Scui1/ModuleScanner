package patternsearching

import pefile.PEFile
import pefile.Section

object PatternSearcher {

    object SearchDirection {
        const val DOWN = 1
        const val UP = -1
    }

    fun searchPattern(peFile: PEFile, section: Section, bytePattern: List<PatternByte>, wantedOccurrences: Int,
                      start: Int = section.rawBase, maxBytesToSearch: Int = section.size, searchDirection: Int = SearchDirection.DOWN): Int {
        var occurrences = 0
        var foundAddress = 0
        var bytesMatched = 0
        for (i in start towards start + (maxBytesToSearch * searchDirection)) {
            val currentByte = peFile.bytes[i]
            if (bytePattern[bytesMatched].isWildcard || currentByte.toUByte() == bytePattern[bytesMatched].value) {
                if (bytesMatched == 0)
                    foundAddress = i

                ++bytesMatched
            } else {
                bytesMatched = 0
                foundAddress = 0
            }

            if (bytesMatched >= bytePattern.size) {
                ++occurrences

                if (occurrences < wantedOccurrences) {
                    bytesMatched = 0
                    foundAddress = 0
                }
            }

            if (occurrences >= wantedOccurrences)
                return foundAddress
        }

        return 0
    }

    // https://stackoverflow.com/a/52986053
    private infix fun Int.towards(to: Int): IntProgression {
        val step = if (this > to) -1 else 1
        return IntProgression.fromClosedRange(this, to, step)
    }
}