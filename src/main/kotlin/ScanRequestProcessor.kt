import json.scanrequest.ScanRequest
import java.io.File
import java.io.IOException

fun processScanRequest(scanRequest: ScanRequest): json.scanresult.ScanResult {
    val scanResult = json.scanresult.ScanResult()

    for (module in scanRequest.modules) {
        val inputFile = File(scanRequest.modulePath).resolve(module.name)

        // TODO: add another json field for errors per module?
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

        val moduleProcessor = ModuleProcessor(moduleBytes, module, scanResult)
        moduleProcessor.process()
    }

    return scanResult
}