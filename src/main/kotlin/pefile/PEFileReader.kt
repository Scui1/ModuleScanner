package pefile

class PEFileReader(private val bytes: ByteArray) {

    fun readInt(base: Int): Int {
        return readIntWithSize(base, 4)
    }

    fun readShort(base: Int): Int = readIntWithSize(base, 2)

    fun readIntWithSize(base: Int, size: Int): Int {
        return convertLittleEndianByteArrayToInt(read(base, size))
    }

    fun readString(base: Int, size: Int): String {
        return String(read(base, size)).trim { it <= ' ' } // remove null characters
    }

    private fun read(base: Int, size: Int): ByteArray {
        return bytes.copyOfRange(base, base + size)
    }

    private fun convertLittleEndianByteArrayToInt(byteArray: ByteArray): Int  {
        var result = 0
        byteArray.reversedArray().forEach { byte ->
            result = (result shl 8) + (byte.toUByte() and 0xFF.toUByte()).toInt()
        }
        return result
    }
}