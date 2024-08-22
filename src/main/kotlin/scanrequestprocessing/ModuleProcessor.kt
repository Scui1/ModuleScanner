import json.PatternType
import json.scanrequest.Module
import json.scanresult.ScanError
import json.scanresult.ScanResult
import scanrequestprocessing.ModuleReader
import scanrequestprocessing.PatternResult
import scanrequestprocessing.processPattern

fun processModule(moduleConfig: Module, output: ScanResult) {
    val peFile = ModuleReader.readModulePEFile(moduleConfig.name) ?: return

    moduleConfig.patterns.forEach { pattern ->
        when (val patternResult = processPattern(pattern, peFile)) {
            is PatternResult.Error -> {
                output.errors.add(ScanError(moduleConfig.name, pattern.type, pattern.name, patternResult.message))
            }
            is PatternResult.Success -> {
                when (pattern.type) {
                    PatternType.FUNCTION -> output.getFunctionsForModule(moduleConfig.name)[pattern.name] = patternResult.value
                    PatternType.ADDRESS -> output.getAddressesForModule(moduleConfig.name)[pattern.name] = patternResult.value
                    PatternType.INDEX -> output.vfunc[pattern.name] = patternResult.value
                    PatternType.OFFSET -> output.offset[pattern.name] = patternResult.value
                }
            }
        }
    }
}