import json.scanrequest.ScanRequest
import scanrequestprocessing.ModuleReader

fun processScanRequest(scanRequest: ScanRequest): json.scanresult.ScanResult {
    val scanResult = json.scanresult.ScanResult()

    for (module in scanRequest.modules) {
        val peFile = ModuleReader.readModulePEFile(module.name)
        if (peFile != null)
            processModule(peFile, module, scanResult)
    }

    return scanResult
}