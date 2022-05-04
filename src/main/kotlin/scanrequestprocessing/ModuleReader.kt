package scanrequestprocessing

import PropertiesReader
import pefile.PEFile
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

object ModuleReader {

    private var moduleDirectory: String

    init {
        val directoryProp = PropertiesReader.getProperty("moduleDirectory")
        if (directoryProp != null && File(directoryProp).exists()) {
            moduleDirectory = directoryProp
        } else {
            println("Mdoule directory not found")
            exitProcess(1)
        }
    }

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
}