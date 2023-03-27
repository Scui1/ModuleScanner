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
}