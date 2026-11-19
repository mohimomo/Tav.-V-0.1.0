package com.example.data

import com.example.model.AlarmItem
import com.example.model.ComponentItem
import com.example.model.ComponentSchematicDetail
import com.example.model.MaintenanceTask
import com.example.model.PidSymbolItem
import com.example.model.SubsystemCategory

sealed class SearchResultItem {
    data class AlarmResult(val alarm: AlarmItem, val matchField: String) : SearchResultItem()
    data class ComponentResult(val component: ComponentItem, val matchField: String) : SearchResultItem()
    data class SchematicResult(val schematic: ComponentSchematicDetail, val component: ComponentItem?, val matchField: String) : SearchResultItem()
    data class PidSymbolResult(val pidSymbol: PidSymbolItem, val matchField: String) : SearchResultItem()
    data class MaintenanceResult(val task: MaintenanceTask, val matchField: String) : SearchResultItem()
}

object FurnaceRepository {
    val alarms: List<AlarmItem> = AlarmsData.list
    val components: List<ComponentItem> = ComponentsData.list
    val schematics: List<ComponentSchematicDetail> = ComponentSchematicsData.list
    val pidSymbols: List<PidSymbolItem> = PidDictionaryData.list
    val maintenanceTasks: List<MaintenanceTask> = MaintenanceData.list
    val specGroups = FurnaceSpecsData.groups

    fun findAlarmById(id: String): AlarmItem? = alarms.find { it.id.equals(id, ignoreCase = true) }

    fun findComponentByTag(tag: String): ComponentItem? = components.find {
        it.tag.contains(tag, ignoreCase = true) || it.pidCode.contains(tag, ignoreCase = true)
    }

    /**
     * Smart Multi-Field Search Engine
     * Searches across components, alarms, P&ID acronyms, and maintenance procedures
     * Supporting Arabic, English, and technical tags/codes.
     */
    fun smartSearch(
        query: String,
        category: SubsystemCategory = SubsystemCategory.ALL
    ): List<SearchResultItem> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) {
            // When query is empty, return categorized items if category is filtered, else top overview
            val results = mutableListOf<SearchResultItem>()
            val filteredAlarms = if (category == SubsystemCategory.ALL) alarms else alarms.filter { it.subsystem == category }
            filteredAlarms.forEach { results.add(SearchResultItem.AlarmResult(it, "Category")) }
            val filteredComponents = if (category == SubsystemCategory.ALL) components else components.filter { it.subsystem == category }
            filteredComponents.forEach { results.add(SearchResultItem.ComponentResult(it, "Category")) }
            return results
        }

        val tokens = trimmed.lowercase().split("\\s+".toRegex()).filter { it.isNotBlank() }
        val results = mutableListOf<SearchResultItem>()

        // 1. Search in Alarms
        for (alarm in alarms) {
            if (category != SubsystemCategory.ALL && alarm.subsystem != category) continue
            val searchHaystack = buildString {
                append(alarm.id).append(" ")
                append(alarm.nameEn).append(" ")
                append(alarm.nameAr).append(" ")
                append(alarm.descriptionEn).append(" ")
                append(alarm.descriptionAr).append(" ")
                append(alarm.reactionEn).append(" ")
                append(alarm.reactionAr).append(" ")
                append(alarm.causesAndSolutionsEn).append(" ")
                append(alarm.causesAndSolutionsAr).append(" ")
                alarm.references.forEach { append(it).append(" ") }
                append(alarm.pageRef)
            }.lowercase()

            val matchesAllTokens = tokens.all { token -> searchHaystack.contains(token) }
            if (matchesAllTokens) {
                val matchField = when {
                    tokens.any { alarm.id.contains(it, ignoreCase = true) } -> "Alarm Code"
                    tokens.any { alarm.nameEn.contains(it, ignoreCase = true) || alarm.nameAr.contains(it, ignoreCase = true) } -> "Alarm Title"
                    tokens.any { alarm.references.any { ref -> ref.contains(it, ignoreCase = true) } } -> "Device Reference Tag"
                    tokens.any { alarm.causesAndSolutionsEn.contains(it, ignoreCase = true) || alarm.causesAndSolutionsAr.contains(it, ignoreCase = true) } -> "Solution / Cause"
                    else -> "Description"
                }
                results.add(SearchResultItem.AlarmResult(alarm, matchField))
            }
        }

        // 2. Search in Components
        for (comp in components) {
            if (category != SubsystemCategory.ALL && comp.subsystem != category) continue
            val searchHaystack = buildString {
                append(comp.tag).append(" ")
                append(comp.pidCode).append(" ")
                append(comp.nameEn).append(" ")
                append(comp.nameAr).append(" ")
                append(comp.manufacturerModel).append(" ")
                append(comp.specifications).append(" ")
                append(comp.electricalCabinet).append(" ")
                append(comp.descriptionEn).append(" ")
                append(comp.descriptionAr).append(" ")
                comp.relatedAlarms.forEach { append(it).append(" ") }
            }.lowercase()

            val matchesAllTokens = tokens.all { token -> searchHaystack.contains(token) }
            if (matchesAllTokens) {
                val matchField = when {
                    tokens.any { comp.tag.contains(it, ignoreCase = true) } -> "Component Tag"
                    tokens.any { comp.pidCode.contains(it, ignoreCase = true) } -> "P&ID Code"
                    tokens.any { comp.nameEn.contains(it, ignoreCase = true) || comp.nameAr.contains(it, ignoreCase = true) } -> "Component Name"
                    tokens.any { comp.manufacturerModel.contains(it, ignoreCase = true) } -> "Manufacturer & Model"
                    else -> "Specifications"
                }
                results.add(SearchResultItem.ComponentResult(comp, matchField))
            }
        }

        // 3. Search in Schematics & Engineering Blueprints
        for (sch in schematics) {
            val linkedComp = components.find { it.id == sch.componentId }
            if (category != SubsystemCategory.ALL && linkedComp != null && linkedComp.subsystem != category) continue

            val searchHaystack = buildString {
                append(sch.tag).append(" ")
                append(sch.pidCode).append(" ")
                append(sch.drawingCode).append(" ")
                append(sch.drawingTitleAr).append(" ")
                append(sch.drawingTitleEn).append(" ")
                append(sch.schematicSheetPage).append(" ")
                append(sch.cabinet).append(" ")
                append(sch.terminalBlock).append(" ")
                append(sch.plcAddressTag).append(" ")
                append(sch.mechanicalMounting).append(" ")
                append(sch.pneumaticOrFluidLine).append(" ")
                append(sch.interlockPermissiveAr).append(" ")
                append(sch.interlockPermissiveEn).append(" ")
                append(sch.flowDirectionDescriptionAr).append(" ")
                append(sch.flowDirectionDescriptionEn).append(" ")
                append(sch.schematicType.titleAr).append(" ")
                append(sch.schematicType.titleEn).append(" ")
                linkedComp?.let {
                    append(it.nameAr).append(" ")
                    append(it.nameEn).append(" ")
                }
            }.lowercase()

            val matchesAllTokens = tokens.all { token -> searchHaystack.contains(token) }
            if (matchesAllTokens) {
                val matchField = when {
                    tokens.any { sch.drawingCode.contains(it, ignoreCase = true) } -> "Drawing Code (DWG)"
                    tokens.any { sch.tag.contains(it, ignoreCase = true) } -> "Component Tag"
                    tokens.any { sch.plcAddressTag.contains(it, ignoreCase = true) } -> "PLC I/O Tag"
                    tokens.any { sch.terminalBlock.contains(it, ignoreCase = true) } -> "Terminal Block"
                    tokens.any { sch.cabinet.contains(it, ignoreCase = true) } -> "Cabinet Location"
                    tokens.any { sch.schematicSheetPage.contains(it, ignoreCase = true) } -> "Schematic Sheet / Page"
                    tokens.any { sch.drawingTitleAr.contains(it, ignoreCase = true) || sch.drawingTitleEn.contains(it, ignoreCase = true) } -> "Drawing Title"
                    else -> "Schematic / Blueprint"
                }
                results.add(SearchResultItem.SchematicResult(sch, linkedComp, matchField))
            }
        }

        // 4. Search in P&ID Symbols
        if (category == SubsystemCategory.ALL) {
            for (pid in pidSymbols) {
                val searchHaystack = "${pid.symbol} ${pid.italian} ${pid.english} ${pid.arabic} ${pid.category}".lowercase()
                if (tokens.all { searchHaystack.contains(it) }) {
                    results.add(SearchResultItem.PidSymbolResult(pid, "P&ID Symbol"))
                }
            }
        }

        // 5. Search in Maintenance Tasks
        for (task in maintenanceTasks) {
            val searchHaystack = buildString {
                append(task.frequencyEn).append(" ")
                append(task.frequencyAr).append(" ")
                append(task.operationEn).append(" ")
                append(task.operationAr).append(" ")
                append(task.remarksEn).append(" ")
                append(task.remarksAr).append(" ")
                task.torques.forEach { append(it).append(" ") }
            }.lowercase()

            if (tokens.all { searchHaystack.contains(it) }) {
                results.add(SearchResultItem.MaintenanceResult(task, "Maintenance Schedule"))
            }
        }

        return results
    }
}
