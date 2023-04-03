package pefile

class PEFile32Architecture() : IPEFileArchitecture {

    override fun getImageBaseOffset(): Int {
        return 0x34
    }

    override fun getDataDirectoriesOffset(): Int {
        return 0x78
    }

    override fun getNumberOfRvaAndSizesOffset(): Int {
        return 0x74
    }

    override fun getVTableEntrySize(): Int {
        return 4
    }

    override fun is64Bit(): Boolean {
        return false
    }

    override fun is32Bit(): Boolean {
        return true
    }
}