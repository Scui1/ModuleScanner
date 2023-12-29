package scanrequestprocessing

import io.ktor.server.config.*
import org.slf4j.LoggerFactory
import pefile.PEFile
import pefile.fromFile
import java.io.File

private val logger = LoggerFactory.getLogger("ModuleProcessor")

object ModuleReader {

    var moduleDirectory: String = ""
        set(value) {
            val file = File(value)
            if (file.exists() && file.isDirectory)
                field = value
            else
                throw ApplicationConfigurationException("Module dir $value doesn't exist")
        }

    fun readModulePEFile(moduleName: String): PEFile? {
        val filePath = File(moduleDirectory).resolve(moduleName).toString()
        val peFile = PEFile.fromFile(filePath)
        if (peFile == null) {
            logger.error("Couldn't read $filePath as PE File.")
        }

        return peFile
    }
}