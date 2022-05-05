package scanrequestprocessing

import io.ktor.server.config.*
import pefile.PEFile
import java.io.File
import java.io.IOException

object ModuleReader {

    var moduleDirectory: String = ""
        set(value)  {
            val file = File(value)
            if (file.exists() && file.isDirectory)
                field = value
            else
                throw ApplicationConfigurationException("Module dir $value doesn't exist")
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