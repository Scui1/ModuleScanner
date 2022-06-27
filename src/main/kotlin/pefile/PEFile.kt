package pefile

import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("PEFile")

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
            logger.info("Failed to find .text section, this shouldn't happen")
            return 0
        }

        val virtualRawDifference = textSection.virtualBase - textSection.rawBase
        return offset + virtualRawDifference
    }

    private fun convertLittleEndianByteArrayToInt(byteArray: ByteArray): Int  {
        var result = 0
        byteArray.reversedArray().forEach { byte ->
            result = (result shl 8) + (byte.toUByte() and 0xFF.toUByte()).toInt()
        }
        return result
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

    fun readInt(base: Int): Int {
        return readIntWithSize(base, 4)
    }

    fun readIntWithSize(base: Int, size: Int): Int {
        return convertLittleEndianByteArrayToInt(read(base, size))
    }

    private fun readShort(base: Int): Int = readIntWithSize(base, 2)
    private fun readString(base: Int, size: Int): String {
        return String(read(base, size)).trim { it <= ' ' } // remove null characters
    }

    private fun read(base: Int, size: Int): ByteArray {
        return bytes.copyOfRange(base, base + size)
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