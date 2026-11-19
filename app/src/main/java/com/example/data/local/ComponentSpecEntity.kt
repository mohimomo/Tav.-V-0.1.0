package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Database Entity to store the technical specifications and schema paths
 * for each component of the TAV TPH 120/120/230 industrial vacuum furnace.
 */
@Entity(tableName = "component_specs")
data class ComponentSpecEntity(
    @PrimaryKey
    val componentId: String,                    // e.g. "CMP_001"
    val tag: String,                            // e.g. "6GQZ10"
    val pidCode: String,                        // e.g. "MePu1"
    val nameEn: String,                         // Component English Name
    val nameAr: String,                         // Component Arabic Name
    val subsystem: String,                      // e.g. "VACUUM", "HEATING", "WATER_COOLING", "PNEUMATIC_GAS"
    val manufacturerModel: String,              // e.g. "Leybold SOGEVAC SV 630 B"
    val specifications: String,                 // Detailed technical ratings & operational parameters
    val electricalCabinet: String,              // Physical panel location (e.g. "Main Cabinet (+MiCa) / Skid")
    val schematicsPage: String,                 // Manual sheet reference (e.g. "MiCa P. 34, 35 / FuCa P. 12")
    val drawingCode: String,                    // Blueprint drawing code (e.g. "DWG-21702-VAC-01")
    val drawingTitleEn: String,                 // Blueprint title in English
    val drawingTitleAr: String,                 // Blueprint title in Arabic
    val schematicDrawingType: String,           // e.g. "ELECTRICAL_WIRING", "P_AND_ID", "PNEUMATIC_VALVE"
    val schemaFilePath: String,                 // Schema file path (e.g. "schematics/DWG-21702-VAC-01.dwg" or local URI)
    val terminalBlock: String,                  // Pinout and wire connections (e.g. "-X2: 1-2-3, -X12: 10-11")
    val plcAddressTag: String,                  // PLC I/O address tag (e.g. "%Q4.0 (Contactor 101KM1)")
    val mechanicalMounting: String,             // Flange dimensions, mounting bolts, and ports
    val pneumaticOrFluidLine: String,           // Fluid, water cooling or gas line specs
    val interlockPermissiveAr: String,          // Operating interlock safety conditions (Arabic)
    val interlockPermissiveEn: String,          // Operating interlock safety conditions (English)
    val flowDirectionDescriptionAr: String,     // Fluid/gas flow path description (Arabic)
    val flowDirectionDescriptionEn: String,     // Fluid/gas flow path description (English)
    val circuitDomain: String,                  // e.g. "ELECTRICAL", "WATER_COOLING", "GAS_PNEUMATIC", "HYDRAULIC_DOOR"
    val circuitNodeId: String?,                 // Reference node in the interactive circuit simulation
    val customNotes: String = "",               // Maintenance and field calibration notes
    val isVerified: Boolean = true,             // Certified OEM engineering verification
    val updatedAt: Long = System.currentTimeMillis()
)
