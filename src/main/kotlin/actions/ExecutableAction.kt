package actions

import pefile.PEFile

interface ExecutableAction {
    val name: String

    @kotlin.jvm.Throws(ActionException::class)
    fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): Int
}