package actions

import pefile.PEFile

interface ExecutableAction {
    val name: String
    fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int
}