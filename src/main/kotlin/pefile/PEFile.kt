package pefile

import java.nio.ByteBuffer
import java.nio.ByteOrder

class PEFile(val bytes: ByteArray) {
    private val sections = getModuleSections()

    fun isValid(): Boolean {
        val str = "${this.bytes[0].toInt().toChar()}${this.bytes[1].toInt().toChar()}"
        val machineType = readShort(getPeHeaderOffset() + 4)

        return str == "MZ" && machineType == 0x14C // Intel 386
    }

    fun getSectionByName(name: String): Section? {
        return sections.find { it.name == name }
    }

    fun convertRawOffsetToVirtualOffset(offset: Int, sectionName: String): Int {
        // fix difference between virtual and raw address, as we need the offset to the pattern in memory
        val textSection = getSectionByName(sectionName)
        if (textSection == null) {
            println("Failed to find .text section, this shouldn't happen")
            return 0
        }

        val virtualRawDifference = textSection.virtualBase - textSection.rawBase
        return offset + virtualRawDifference
    }

    fun getImageBase(): Int {
        return readInt(getPeHeaderOffset() + 0x34)
    }

    private fun getPeHeaderOffset(): Int {
        return readInt(0x3C)
    }

    private fun getNumberOfSections(): Int {
        return readShort(getPeHeaderOffset() + 6)
    }

    private fun getDataDirectoriesOffset(): Int {
        return getPeHeaderOffset() + 0x78
    }

    private fun getNumberOfRvaAndSizes(): Int {
        return readInt(getPeHeaderOffset() + 0x74)
    }

    private fun readInt(base: Int): Int {
        return read(base, 4).int
    }

    private fun readShort(base: Int): Int {
        return read(base, 2).short.toInt()
    }

    private fun readString(base: Int, size: Int): String {
        return String(read(base, size).array()).trim { it <= ' ' } // remove null characters
    }

    private fun read(base: Int, size: Int): ByteBuffer {
        val targetBytes = bytes.copyOfRange(base, base + size)

        val byteBuffer = ByteBuffer.wrap(targetBytes)
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
        return byteBuffer
    }

    private fun getModuleSections(): List<Section> {
        val sections = mutableListOf<Section>()

        val sectionTableOffset = getDataDirectoriesOffset() + 8 * getNumberOfRvaAndSizes()

        for (i in 0 until getNumberOfSections()) {
            val sectionEntry = sectionTableOffset + 40 * i
            val name = readString(sectionEntry, 8)
            val size = readInt(sectionEntry + 8)
            val virtualBase = readInt(sectionEntry + 12)
            val rawBase = readInt(sectionEntry + 20)

            val section = Section(name, rawBase, virtualBase, size)
            sections.add(section)
        }

        return sections
    }
}