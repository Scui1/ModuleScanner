import json.scanrequest.Module
import json.scanresult.ScanResult
import scanrequestprocessing.ModuleReader
import scanrequestprocessing.processPattern

fun processModule(moduleConfig: Module, output: ScanResult) {
    val peFile = ModuleReader.readModulePEFile(moduleConfig.name)
        ?: return

    moduleConfig.patterns.forEach { pattern -> processPattern(peFile, moduleConfig.name, pattern, output) }
}