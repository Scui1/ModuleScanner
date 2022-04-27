import java.io.File
import java.io.IOException

fun main() {
    val config = readConfig()?: return

    for (module in config.modules) {
        val inputFile = File(config.modulePath).resolve(module.name)

        val moduleBytes: ByteArray
        try {
            moduleBytes = inputFile.readBytes()
        } catch (e: IOException) {
            println("Module ${inputFile.name} couldn't be read: ${e.message}")
            continue
        }

        if (moduleBytes.isEmpty()) {
            println("Module ${inputFile.name} couldn't be read")
            continue
        }

        val moduleProcessor = ModuleProcessor(moduleBytes, module)
        moduleProcessor.process()
    }

}