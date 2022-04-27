package pefile

data class Section(val name: String, val rawBase: Int, val virtualBase: Int, val size: Int)
