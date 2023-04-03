package pefile

class PEFile64Architecture() : IPEFileArchitecture {

    override fun getImageBaseOffset(): Int {
        return 0x30
    }

    override fun getDataDirectoriesOffset(): Int {
        return 0x88
    }

    override fun getNumberOfRvaAndSizesOffset(): Int {
        return 0x84
    }

    override fun getVTableEntrySize(): Int {
        return 8
    }

    override fun is64Bit(): Boolean {
        return true
    }

    override fun is32Bit(): Boolean {
        return false
    }
}