import json.PatternType
import json.scanrequest.Action
import json.scanrequest.Module
import json.scanrequest.Pattern
import json.scanrequest.ScanRequest
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeEach
import scanrequestprocessing.ModuleReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import kotlin.system.measureTimeMillis
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class PatternScanTest {

    private val FUNCTION_NAME = "TopLevelExceptionFilter"
    private val X86_MODULE_NAME = "crashhandler.dll"
    private val X64_MODULE_NAME = "crashhandler64.dll"

    @BeforeEach
    fun setupModulesDir() {
        ModuleReader.moduleDirectory = PatternScanTest::class.java.getResource("moduleDirectory")?.path ?: ""
    }

    @Test
    fun patternScanFunction() {
        val x86Module = Module(
            X86_MODULE_NAME,
            listOf(
                Pattern(
                    FUNCTION_NAME,
                    PatternType.FUNCTION,
                    listOf(Action("PatternSearch", listOf("55 8B EC 83 E4 F8 51")))
                )
            )
        )
        val x64Module = Module(
            X64_MODULE_NAME,
            listOf(
                Pattern(
                    FUNCTION_NAME,
                    PatternType.FUNCTION,
                    listOf(Action("PatternSearch", listOf("48 89 5C 24 ?? 57 48 83 EC 20 48 8B F9 48 8D 0D")))
                )
            )
        )

        val result = processScanRequest(ScanRequest(listOf(x86Module, x64Module)))

        assertEquals(9091, result.function[X86_MODULE_NAME]?.get(FUNCTION_NAME))
        assertEquals(27024, result.function[X64_MODULE_NAME]?.get(FUNCTION_NAME))
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun performanceTest() {
        val scanRequestText = PatternScanTest::class.java.getResource("clientDllScanRequest.json")?.readText(StandardCharsets.UTF_8) ?: fail("Couldn't get clientDllScanRequest")
        val scanRequest = Json.decodeFromString<ScanRequest>(scanRequestText)


        val resultTime = measureTime {
            processScanRequest(scanRequest)
        }

        assertTrue(resultTime < 5.seconds)

        println("Performance test took: $resultTime")
    }
}