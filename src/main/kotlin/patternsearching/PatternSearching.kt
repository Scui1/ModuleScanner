package patternsearching

import pefile.PEFile
import pefile.Section

fun searchPattern(
    peFile: PEFile, section: Section, bytePattern: List<PatternByte>, wantedOccurrences: Int,
    start: Int = section.rawBase, maxBytesToSearch: Int = section.size
): Int {
    var occurrences = 0
    var foundAddress = 0
    var bytesMatched = 0
    for (i in start until start + maxBytesToSearch) {
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