package pefile

interface IPEFileArchitecture {
    fun getImageBaseOffset(): Int
    fun getDataDirectoriesOffset(): Int
    fun getNumberOfRvaAndSizesOffset(): Int
    fun getVTableEntrySize(): Int
}