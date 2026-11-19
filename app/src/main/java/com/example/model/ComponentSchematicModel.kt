package com.example.model

enum class SchematicDrawingType(
    val titleEn: String,
    val titleAr: String,
    val iconName: String
) {
    P_AND_ID("P&ID Flowchart", "مخطط الأنابيب والأجهزة P&ID", "schema"),
    ELECTRICAL_WIRING("Electrical Wiring Loop", "مخطط التوصيل الكهربائي والأسلاك", "electrical_services"),
    MECHANICAL_FLANGE("Mechanical Section & Mount", "الرسم المقطعي الميكانيكي وأبعاد الربط", "architecture"),
    PNEUMATIC_VALVE("Pneumatic Control Flow", "مخطط التحكم النيوماتيكي والصمامات", "air"),
    PLC_IO_LOOP("PLC Automation & Signal Loop", "مسار إشارة الأتمتة وحلقات الـ PLC", "developer_board")
}

data class ComponentSchematicDetail(
    val id: String,
    val componentId: String,
    val tag: String,
    val pidCode: String,
    val drawingCode: String,
    val drawingTitleAr: String,
    val drawingTitleEn: String,
    val schematicType: SchematicDrawingType,
    val schematicSheetPage: String,
    val cabinet: String,
    val terminalBlock: String,
    val plcAddressTag: String,
    val mechanicalMounting: String,
    val pneumaticOrFluidLine: String,
    val interlockPermissiveAr: String,
    val interlockPermissiveEn: String,
    val flowDirectionDescriptionAr: String,
    val flowDirectionDescriptionEn: String,
    val circuitDomain: CircuitDomain? = null,
    val circuitNodeId: String? = null,
    val keySymbols: List<String> = emptyList()
)
