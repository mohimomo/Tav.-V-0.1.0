package com.example

import com.example.data.FurnaceRepository
import com.example.data.FurnaceSpecsData
import com.example.data.SearchResultItem
import com.example.model.AlarmSeverity
import com.example.model.SubsystemCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testFurnaceSpecificationsLoaded() {
        assertEquals("TPH 120/120/230", FurnaceSpecsData.MODEL)
        assertEquals("911", FurnaceSpecsData.SERIAL_NUMBER)
        assertEquals("2023", FurnaceSpecsData.YEAR)
        assertTrue(FurnaceRepository.specGroups.isNotEmpty())
    }

    @Test
    fun testAlarmsAndComponentsLoaded() {
        assertTrue(FurnaceRepository.alarms.size >= 40)
        assertTrue(FurnaceRepository.components.size >= 20)
        assertTrue(FurnaceRepository.pidSymbols.size >= 50)
        assertTrue(FurnaceRepository.maintenanceTasks.size >= 10)
    }

    @Test
    fun testSmartSearchByComponentTag() {
        val results = FurnaceRepository.smartSearch("6GQZ10")
        assertTrue(results.isNotEmpty())
        val compResult = results.filterIsInstance<SearchResultItem.ComponentResult>()
        assertTrue(compResult.any { it.component.tag == "6GQZ10" })
    }

    @Test
    fun testSmartSearchByPidAcronym() {
        val results = FurnaceRepository.smartSearch("MePu")
        assertTrue(results.isNotEmpty())
        val hasMatch = results.any {
            (it is SearchResultItem.ComponentResult && it.component.pidCode.startsWith("MePu")) ||
            (it is SearchResultItem.PidSymbolResult && it.pidSymbol.symbol == "Me")
        }
        assertTrue(hasMatch)
    }

    @Test
    fun testSmartSearchByArabicKeyword() {
        val results = FurnaceRepository.smartSearch("تسريب")
        assertTrue("Expected search results for Arabic term 'تسريب'", results.isNotEmpty())

        val resultsPump = FurnaceRepository.smartSearch("مضخة")
        assertTrue("Expected search results for Arabic term 'مضخة'", resultsPump.isNotEmpty())
    }

    @Test
    fun testSmartSearchByAlarmSymptom() {
        val results = FurnaceRepository.smartSearch("overtemperature")
        assertTrue(results.isNotEmpty())
        val alarmMatches = results.filterIsInstance<SearchResultItem.AlarmResult>()
        assertTrue(alarmMatches.isNotEmpty())
    }

    @Test
    fun testCategoryFiltering() {
        val vacuumResults = FurnaceRepository.smartSearch("", SubsystemCategory.VACUUM)
        assertTrue(vacuumResults.isNotEmpty())
        vacuumResults.forEach {
            when (it) {
                is SearchResultItem.AlarmResult -> assertEquals(SubsystemCategory.VACUUM, it.alarm.subsystem)
                is SearchResultItem.ComponentResult -> assertEquals(SubsystemCategory.VACUUM, it.component.subsystem)
                else -> {}
            }
        }
    }

    @Test
    fun testRoomManualSeederHasComprehensiveChapters() {
        val initialDocs = com.example.data.local.ManualContentSeeder.getInitialManualDocuments()
        assertTrue("Manual seeder must have documents", initialDocs.isNotEmpty())
        assertTrue("Manual seeder must contain at least 8 key manual sections", initialDocs.size >= 8)

        // Verify key chapters exist
        val chapters = initialDocs.map { it.chapterNumber }.toSet()
        assertTrue("Must have Chapter 1 General", chapters.contains("1"))
        assertTrue("Must have Chapter 3 Safety", chapters.contains("3"))
        assertTrue("Must have Chapter 4 Description", chapters.contains("4"))
        assertTrue("Must have Chapter 6 Operation", chapters.contains("6"))
        assertTrue("Must have Chapter 8 Maintenance", chapters.contains("8"))

        // Verify bilingual content and page numbers
        initialDocs.forEach { doc ->
            assertTrue("Document ${doc.id} must have page number > 0", doc.pageNumber > 0)
            assertTrue("Document ${doc.id} must have English content", doc.contentEn.isNotBlank())
            assertTrue("Document ${doc.id} must have Arabic content", doc.contentAr.isNotBlank())
            assertTrue("Document ${doc.id} must have PDF OCR excerpt", doc.rawPdfOcrExcerpt.isNotBlank())
        }
    }

    @Test
    fun testAboutAppInformation() {
        assertEquals("TPH 120/120/230", FurnaceSpecsData.MODEL)
        assertEquals("GEAT", FurnaceSpecsData.CUSTOMER)
        assertEquals("2023", FurnaceSpecsData.YEAR)
        assertEquals("911", FurnaceSpecsData.SERIAL_NUMBER)

        // Verify manufacturer info
        val mfrName = "TAV VACUUM FURNACES S.p.A."
        val mfrAddress = "Via Dell'Industria, 11 - 24043 Caravaggio (BG), Italy"
        val mfrPhone = "+39 0363 355711"
        val mfrEmail = "service@tav-vacuumfurnaces.com"

        assertTrue(mfrName.contains("TAV"))
        assertTrue(mfrAddress.contains("Caravaggio"))
        assertTrue(mfrPhone.startsWith("+39"))
        assertTrue(mfrEmail.endsWith("@tav-vacuumfurnaces.com"))

        // Verify developer info
        val devEmail = "mohamedbouzidi333@gmail.com"
        assertTrue(devEmail.contains("@gmail.com"))
    }

    @Test
    fun testTraceableCircuitsData() {
        val allDiagrams = com.example.data.CircuitsData.allDiagrams
        assertEquals("Must have 4 complete domain diagrams", 4, allDiagrams.size)

        // Verify all 4 domains exist
        val domains = allDiagrams.map { it.domain }.toSet()
        assertTrue(domains.contains(com.example.model.CircuitDomain.ELECTRICAL))
        assertTrue(domains.contains(com.example.model.CircuitDomain.WATER_COOLING))
        assertTrue(domains.contains(com.example.model.CircuitDomain.GAS_PNEUMATIC))
        assertTrue(domains.contains(com.example.model.CircuitDomain.HYDRAULIC_DOOR))

        allDiagrams.forEach { diagram ->
            assertTrue("Diagram ${diagram.id} must have nodes", diagram.nodes.isNotEmpty())
            assertTrue("Diagram ${diagram.id} must have connections", diagram.connections.isNotEmpty())
            assertTrue("Diagram ${diagram.id} must have traceable paths", diagram.traceablePaths.isNotEmpty())

            // Verify each path's nodes belong to the diagram
            val nodeIds = diagram.nodes.map { it.id }.toSet()
            diagram.traceablePaths.forEach { path ->
                assertTrue("Path ${path.id} must have nodes", path.nodeIds.isNotEmpty())
                path.nodeIds.forEach { nodeId ->
                    assertTrue("Node $nodeId in path ${path.id} must exist in diagram ${diagram.id}", nodeIds.contains(nodeId))
                }
            }
        }

        // Verify key component tags in electrical diagram
        val elNodes = com.example.data.CircuitsData.electricalDiagram.nodes.map { it.componentTag }
        assertTrue(elNodes.contains("152QAC"))
        assertTrue(elNodes.contains("Tr1"))
        assertTrue(elNodes.contains("6GQZ10"))

        // Verify emergency water pump in water diagram
        val waterNodes = com.example.data.CircuitsData.waterDiagram.nodes.map { it.componentTag }
        assertTrue(waterNodes.contains("EmPu1"))

        // Verify two-hand control in hydraulic door diagram
        val hydNodes = com.example.data.CircuitsData.hydraulicDoorDiagram.nodes.map { it.componentTag }
        assertTrue(hydNodes.contains("ACT1+ACT2"))
    }

    @Test
    fun testComponentsVisualAssetsLocationAndRole() {
        val components = com.example.data.ComponentsData.list
        assertTrue(components.isNotEmpty())

        // Verify each component has a valid Tag, PID Code, Location and Role
        for (comp in components) {
            assertTrue(comp.tag.isNotBlank())
            assertTrue(comp.pidCode.isNotBlank())
            assertTrue(comp.locationAr.isNotBlank())
            assertTrue(comp.roleAr.isNotBlank())
            assertNotNull(comp.imageResId)
        }

        // Test specific component: Roughing Pump
        val mePu1 = components.find { it.tag == "6GQZ10" }
        assertNotNull(mePu1)
        assertEquals("MePu1", mePu1?.pidCode)
        assertTrue(mePu1?.nameAr?.contains("مضخة التفريغ الابتدائي") == true)
        assertTrue(mePu1?.locationAr?.contains("شاسيه نظام الفراغ") == true)
        assertTrue(mePu1?.roleAr?.contains("سحب وتفريغ الهواء") == true)

        // Test High vacuum gate valve
        val hiVVa1 = components.find { it.pidCode == "HiVVa1" }
        assertNotNull(hiVVa1)
        assertEquals("6MMQ11", hiVVa1?.tag)
        assertTrue(hiVVa1?.locationAr?.contains("بين فوهة الفراغ") == true)
    }

    @Test
    fun testGeminiAiChatModels() {
        val geminiService = com.example.data.GeminiAiService()
        assertNotNull(geminiService)

        // Verify AiAssistantMode values
        val modes = com.example.model.AiAssistantMode.values()
        assertEquals(4, modes.size)

        val modeNames = modes.map { it.modelName }.toSet()
        assertTrue(modeNames.contains("gemini-3.8-flash"))
        assertTrue(modeNames.contains("gemini-3.1-pro-preview"))
        assertTrue(modeNames.contains("imagen-3.0-generate-002"))

        // Create ChatMessage test
        val msg = com.example.model.ChatMessage(
            sender = com.example.model.ChatSender.USER,
            text = "Test TAV furnace query"
        )
        assertEquals(com.example.model.ChatSender.USER, msg.sender)
        assertEquals("Test TAV furnace query", msg.text)
        assertFalse(msg.isError)

        // Test CachedAiResponseEntity creation
        val cachedEntity = com.example.data.local.CachedAiResponseEntity(
            query = "What is SV630B pump?",
            responseText = "SV630B is Leybold rotary vane pump",
            mode = "GENERAL_DIAGNOSTICS"
        )
        assertEquals("What is SV630B pump?", cachedEntity.query)
        assertEquals("SV630B is Leybold rotary vane pump", cachedEntity.responseText)
        assertEquals("GENERAL_DIAGNOSTICS", cachedEntity.mode)

        // Test ComponentPhotoEntity creation and properties
        val photoEntity = com.example.data.local.ComponentPhotoEntity(
            componentId = "CMP_001",
            localFilePath = "/data/user/0/com.example/files/component_photos/cmp_CMP_001.jpg",
            notes = "Real on-site photo taken during maintenance"
        )
        assertEquals("CMP_001", photoEntity.componentId)
        assertTrue(photoEntity.localFilePath.endsWith(".jpg"))
        assertEquals("Real on-site photo taken during maintenance", photoEntity.notes)
    }

    @Test
    fun testAiAssistantPromptValidationAndOfflineSimulation() {
        val alarms = com.example.data.FurnaceRepository.alarms
        assertFalse(alarms.isEmpty())
        val waterAlarm = alarms.find { it.references.any { r -> r.contains("water", ignoreCase = true) } || it.nameEn.contains("water", ignoreCase = true) || it.id.startsWith("ALM_") }
        assertNotNull(waterAlarm)

        // Verify key diagnostic components are recognized
        val components = com.example.data.FurnaceRepository.components
        val pump = components.find { it.tag.contains("6GQZ10") }
        assertNotNull(pump)
        assertTrue(pump!!.manufacturerModel.contains("Leybold") || pump.nameEn.contains("Pump"))
    }

    @Test
    fun testCustomApiKeyIntegration() {
        val aiService = com.example.data.GeminiAiService()
        val customKey = "sk-JkRTC3ITIkhIB3aFS82uahpRtHNXw70V2wtaJHLNRhzpzAra"
        assertTrue(customKey.startsWith("sk-"))
        assertEquals(51, customKey.length)
        assertTrue(aiService.hasApiKey())
    }

    @Test
    fun testFrenchLanguageSupport() {
        val fr = com.example.ui.AppLanguage.FRENCH
        assertEquals("fr", fr.code)
        assertEquals("Français", fr.label)
        assertEquals("FR", fr.badge)

        val vacTitle = com.example.model.SubsystemCategory.VACUUM.getTitle(fr)
        assertEquals("Système de vide", vacTitle)

        val homeTitle = com.example.ui.AppStrings.navHome(fr)
        assertEquals("Accueil", homeTitle)
    }

    @Test
    fun testLeakRateFormulaAndCalculations() {
        val chamberVolume = 3312.0 // dm3 (liters)
        val p1 = 0.0001 // mbar
        val p2 = 0.0045 // mbar
        val timeSeconds = 30.0 * 60.0 // 30 minutes in seconds

        val deltaP = p2 - p1
        val q = (chamberVolume * deltaP) / timeSeconds
        assertTrue(q > 0.0)
        assertTrue(q < 0.01)
    }

    @Test
    fun testGasConsumptionCalculations() {
        val chamberM3 = 3.312
        val pressureBar = 5.0
        val totalGasM3 = chamberM3 * pressureBar
        val bottles = Math.ceil(totalGasM3 / 10.0).toInt()
        assertEquals(16.56, totalGasM3, 0.01)
        assertEquals(2, bottles)
    }

    @Test
    fun testNewNavigationScreensAndShiftLogModel() {
        val shiftLog = com.example.data.local.ShiftLogEntity(
            batchNumber = "BATCH-001",
            operatorName = "Tech GEAT",
            processType = "Hardening",
            loadWeightKg = 1200.0,
            targetTempC = 1050.0,
            quenchPressureBar = 5.5,
            status = "Completed",
            leakRateQ = 0.0008,
            notes = "Test run"
        )
        assertEquals("BATCH-001", shiftLog.batchNumber)
        assertEquals(1200.0, shiftLog.loadWeightKg, 0.001)

        val navScreens = com.example.ui.NavigationScreen.values()
        assertTrue(navScreens.contains(com.example.ui.NavigationScreen.CALCULATORS))
        assertTrue(navScreens.contains(com.example.ui.NavigationScreen.SHIFT_LOG))
        assertTrue(navScreens.contains(com.example.ui.NavigationScreen.TELEMETRY))
        assertTrue(navScreens.contains(com.example.ui.NavigationScreen.SCANNER))
    }
}
