import pefile.PEFile
import java.io.File
import java.io.IOException

private const val moduleDirectory = "B:\\Trash\\moduleScannerInput" // TODO: make this configurable

fun readModulePEFile(moduleName: String): PEFile? {
    val inputFile = File(moduleDirectory).resolve(moduleName)

    val moduleBytes: ByteArray
    try {
        moduleBytes = inputFile.readBytes()
    } catch (e: IOException) {
        println("Module $moduleName couldn't be read: ${e.message}")
        return null
    }

    if (moduleBytes.isEmpty()) {
        println("Module $moduleName couldn't be read")
        return null
    }

    val peFile = PEFile(moduleBytes)
    if (!peFile.isValid()) {
        println("Module $moduleName is not a valid pe file, module won't be processed")
        return null
    }

    return peFile
}