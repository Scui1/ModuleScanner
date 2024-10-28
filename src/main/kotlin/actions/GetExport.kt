package actions

import actions.GetExport.Parameters.EXPORT_NAME
import pefile.PEFile
import pefile.datadirectory.DataDirectoryType.EXPORT_DIRECTORY
import pefile.datadirectory.directories.ExportDirectory


object GetExport : ExecutableAction {
    override val name = "GetExport"

    private object Parameters {
        const val EXPORT_NAME = 0
    }

    override fun execute(peFile: PEFile, currentOffset: Int, arguments: List<String>): ActionResult {
        val exportName = if (arguments.isNotEmpty()) arguments[EXPORT_NAME] else throw ActionException("ExportName is missing.")

        val exportDirectory = peFile.getDataDirectoryByType(EXPORT_DIRECTORY)
        if (exportDirectory == null || exportDirectory !is ExportDirectory) {
            throw ActionException("Failed to get export directory.")
        }

        val export = exportDirectory.getExportByName(exportName)
            ?: throw ActionException("Failed to find export '$exportName'.")

        return ActionResult(export.rawFuncAddress)
    }
}