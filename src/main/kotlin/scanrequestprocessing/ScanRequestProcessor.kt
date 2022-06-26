import json.scanrequest.ScanRequest
import json.scanresult.ScanResult

fun processScanRequest(scanRequest: ScanRequest): ScanResult {
    val scanResult = ScanResult()

    scanRequest.modules.forEach { module -> processModule(module, scanResult) }

    return scanResult
}