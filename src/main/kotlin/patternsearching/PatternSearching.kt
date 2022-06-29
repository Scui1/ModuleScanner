package patternsearching

import pefile.PEFile
import pefile.Section

fun searchPattern(
    peFile: PEFile, section: Section, patternArg: List<PatternByte>, wantedOccurrences: Int,
    start: Int = section.rawBase, maxBytesToSearch: Int = section.size, backwards: Boolean = false
): Int {
    val bytePattern = if (backwards) patternArg.reversed() else patternArg
    val endAddress = if (backwards) start - maxBytesToSearch else start + maxBytesToSearch

    var occurrences = 0
    var foundAddress = 0
    var bytesMatched = 0
    for (i in start towards endAddress) {
        val currentByte = peFile.bytes[i].toUByte()
        if (bytePattern[bytesMatched].matches(currentByte)) {
            if (bytesMatched == 0)
                foundAddress = calcFoundAddress(i, bytePattern.size, backwards)

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

private fun calcFoundAddress(currentAddress: Int, patternLength: Int, backwards: Boolean): Int {
    return if (backwards) {
        currentAddress - (patternLength - 1)
    } else {
        currentAddress
    }
}

// https://stackoverflow.com/a/52986053
private infix fun Int.towards(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}