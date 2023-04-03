package pefile

interface IPEFileArchitecture {
    fun getImageBaseOffset(): Int
    fun getDataDirectoriesOffset(): Int
    fun getNumberOfRvaAndSizesOffset(): Int
    fun getVTableEntrySize(): Int

    fun is64Bit(): Boolean
    fun is32Bit(): Boolean
}