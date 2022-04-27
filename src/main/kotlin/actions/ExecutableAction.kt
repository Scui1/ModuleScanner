package actions

import pefile.PEFile

interface ExecutableAction {
    val name: String
    fun execute(peFile: PEFile, arguments: List<String>): Int
}