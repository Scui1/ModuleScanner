package disassemblerequestprocessing

import json.scanresult.ScanResult
import pefile.disassembler.disassembleToString
import scanrequestprocessing.ModuleReader

fun disassembleScanResult(scanResult: ScanResult): Result<String> {
    if (scanResult.errors.isNotEmpty())
        return Result.failure(Exception("Scanresult had errors."))
    else if (scanResult.address.size > 1 || scanResult.function.size > 1)
        return Result.failure(Exception("Scan result has more than 1 addresses to disassemble."))
    else if (scanResult.address.isEmpty() && scanResult.function.isEmpty())
        return Result.failure(Exception("Scan result has no address to disassemble."))

    val moduleResultsEntry = scanResult.address.entries.firstOrNull() ?: scanResult.function.entries.first()
    val moduleResults = moduleResultsEntry.value
    if (moduleResults.size > 1)
        return Result.failure(Exception("Scan result has more than 1 result to disassemble."))
    else if (moduleResults.isEmpty())
        return Result.failure(Exception("Scan result has no result to disassemble."))

    val resultEntry = moduleResults.entries.first()
    val address = resultEntry.value

    val peFile = ModuleReader.readModulePEFile(moduleResultsEntry.key) ?:
        return Result.failure(Exception("Module for result couldn't be read."))

    val section = peFile.getSectionByVirtualAddress(address) ?:
        return Result.failure(Exception("Couldn't find section for address $address."))
    if (section.name != ".text")
        return Result.failure(Exception("Section for address $address ${section.name} is not .text."))

    if (!peFile.virtualAddressHasRawAddress(address))
        return Result.failure(Exception("Virtual address $address has no raw address $address."))

    val rawAddress = peFile.convertVirtualOffsetToRawOffset(address)
    if (rawAddress == 0)
        return Result.failure(Exception("Raw address for $address was not found."))

    return Result.success(peFile.disassembleToString( rawAddress.toLong(), 100))
}