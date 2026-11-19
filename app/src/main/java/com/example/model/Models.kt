package com.example.model

enum class AlarmSeverity {
    CRITICAL,
    HIGH,
    WARNING
}

enum class SubsystemCategory(val titleEn: String, val titleAr: String, val titleFr: String = titleEn) {
    ALL("All", "الكل", "Tous"),
    VACUUM("Vacuum System", "نظام التفريغ", "Système de vide"),
    HEATING("Heating & Thermal", "التسخين والعزل الحراري", "Chauffage & Thermique"),
    WATER_COOLING("Water Cooling", "تبريد المياه والشيلر", "Refroidissement d'eau"),
    PNEUMATIC_GAS("Pneumatics & Gas Feed", "النيوماتيك والغاز", "Pneumatique & Gaz"),
    ELECTRICAL_PLC("Electrical & Automation", "الكهرباء ولوحات التحكم", "Électrique & Automate PLC"),
    DOOR_SAFETY("Door & Mechanical Safety", "الأمان الميكانيكي والباب", "Sécurité porte & Mécanique"),
    PUMPS("Pumps & Motors", "المضخات والمحركات", "Pompes & Moteurs");

    fun getTitle(lang: com.example.ui.AppLanguage): String = when (lang) {
        com.example.ui.AppLanguage.ARABIC -> titleAr
        com.example.ui.AppLanguage.FRENCH -> titleFr
        com.example.ui.AppLanguage.ENGLISH -> titleEn
    }
}

data class AlarmItem(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val reactionEn: String,
    val reactionAr: String,
    val causesAndSolutionsEn: String,
    val causesAndSolutionsAr: String,
    val references: List<String>,
    val subsystem: SubsystemCategory,
    val severity: AlarmSeverity,
    val pageRef: String = ""
)

data class ComponentItem(
    val id: String,
    val tag: String, // e.g. "6GQZ10", "LoVVa1", "S7-1513"
    val pidCode: String, // e.g. "MePu1", "HiVVa1", "VeP1"
    val nameEn: String,
    val nameAr: String,
    val subsystem: SubsystemCategory,
    val manufacturerModel: String,
    val specifications: String,
    val electricalCabinet: String, // e.g. "Main Cabinet (MiCa)", "Furnace Cabinet (FuCa)", "Water Cabinet (WaCa)", "Field"
    val schematicsPage: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val relatedAlarms: List<String> = emptyList(),
    @androidx.annotation.DrawableRes val imageResId: Int? = null,
    val locationEn: String = "",
    val locationAr: String = "",
    val roleEn: String = "",
    val roleAr: String = ""
)

data class PidSymbolItem(
    val symbol: String,
    val italian: String,
    val english: String,
    val arabic: String,
    val category: String
)

data class MaintenanceTask(
    val id: String,
    val frequencyEn: String,
    val frequencyAr: String,
    val operationEn: String,
    val operationAr: String,
    val remarksEn: String,
    val remarksAr: String,
    val requiredToolsEn: String,
    val requiredToolsAr: String,
    val risksEn: String,
    val risksAr: String,
    val responsibility: String,
    val torques: List<String> = emptyList()
)

data class TechnicalSpecGroup(
    val titleEn: String,
    val titleAr: String,
    val items: List<SpecItem>
)

data class SpecItem(
    val labelEn: String,
    val labelAr: String,
    val value: String,
    val unit: String = ""
)
