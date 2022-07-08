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
    var bytesMatched = 0
    for (i in start towards endAddress) {

        if (currentByteMatchesPatternByte(peFile, i, bytePattern[bytesMatched]))
            ++bytesMatched
        else {
            // if the current byte does not match and we had already more than 1 bytes matched, we need to recheck if the current byte is the start of the pattern
            bytesMatched = if (bytesMatched > 0)
                if (currentByteMatchesPatternByte(peFile, i, bytePattern[0])) 1 else 0
            else
                0
        }

        if (bytesMatched >= bytePattern.size) {
            ++occurrences

            if (occurrences < wantedOccurrences)
                bytesMatched = 0
            else
                return calcFoundAddress(i, bytePattern.size, backwards)
        }
    }

    return 0
}

private fun calcFoundAddress(currentAddress: Int, patternLength: Int, backwards: Boolean): Int {
    return if (backwards) {
        currentAddress
    } else {
        currentAddress - (patternLength - 1)
    }
}

private fun currentByteMatchesPatternByte(peFile: PEFile, byteIndex: Int, patternByte: PatternByte): Boolean {
    return patternByte.matches(peFile.bytes[byteIndex].toUByte())
}

// https://stackoverflow.com/a/52986053
private infix fun Int.towards(to: Int): IntProgression {
    val step = if (this > to) -1 else 1
    return IntProgression.fromClosedRange(this, to, step)
}