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
}