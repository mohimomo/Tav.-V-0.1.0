package com.example.model

enum class CircuitDomain(val titleEn: String, val titleAr: String, val iconName: String) {
    ELECTRICAL("Electrical & Power", "الدارات والمخططات الكهربائية", "bolt"),
    WATER_COOLING("Water & Cooling", "الدارات المائية ونظام التبريد", "water_drop"),
    GAS_PNEUMATIC("Gas & Pneumatics", "الدارات الغازية والنيوماتيكية", "air"),
    HYDRAULIC_DOOR("Hydraulic & Door Lock", "الدارات الهيدروليكية وإحكام الباب", "settings")
}

enum class NodeSymbolType {
    POWER_SOURCE,
    TRANSFORMER,
    BREAKER,
    CONTACTOR,
    THYRISTOR,
    HEATING_ELEMENT,
    MOTOR_PUMP,
    VALVE,
    SENSOR,
    PLC_MODULE,
    TANK,
    HEAT_EXCHANGER,
    PISTON,
    SAFETY_STATION,
    VESSEL
}

enum class FlowType {
    ELECTRIC_3PHASE,
    ELECTRIC_CONTROL,
    WATER_SUPPLY,
    WATER_RETURN,
    WATER_EMERGENCY,
    GAS_HIGH_PRESSURE,
    PNEUMATIC_AIR,
    HYDRAULIC_FLUID,
    SAFETY_SIGNAL
}

data class CircuitNode(
    val id: String,
    val labelEn: String,
    val labelAr: String,
    val componentTag: String, // e.g. "152QAC", "MePu1", "Tr1"
    val symbolType: NodeSymbolType,
    val defaultStatusEn: String,
    val defaultStatusAr: String,
    val x: Float, // 0f .. 1000f
    val y: Float, // 0f .. 600f
    val rating: String,
    val cabinet: String,
    val schematicsPage: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val relatedAlarms: List<String> = emptyList(),
    val isPrimarySource: Boolean = false,
    val isEndpoint: Boolean = false
)

data class CircuitConnection(
    val fromNodeId: String,
    val toNodeId: String,
    val flowType: FlowType,
    val labelEn: String = "",
    val labelAr: String = ""
)

data class TraceablePath(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val descriptionEn: String,
    val descriptionAr: String,
    val nodeIds: List<String>,
    val flowColorHex: Long = 0xFF00E5FF
)

data class CircuitDiagram(
    val id: String,
    val domain: CircuitDomain,
    val titleEn: String,
    val titleAr: String,
    val subtitleEn: String,
    val subtitleAr: String,
    val schematicsRef: String,
    val nodes: List<CircuitNode>,
    val connections: List<CircuitConnection>,
    val traceablePaths: List<TraceablePath>
)
