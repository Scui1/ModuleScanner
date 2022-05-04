import json.scanrequest.ScanRequest

fun processScanRequest(scanRequest: ScanRequest): json.scanresult.ScanResult {
    val scanResult = json.scanresult.ScanResult()

    for (module in scanRequest.modules) {
        val peFile = readModulePEFile(module.name)
        if (peFile != null)
            processModule(peFile, module, scanResult)
    }

    return scanResult
}