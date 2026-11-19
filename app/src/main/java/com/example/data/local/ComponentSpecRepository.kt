package com.example.data.local

import android.content.Context
import com.example.data.ComponentSchematicsData
import com.example.data.ComponentsData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repository to manage local persistence for furnace technical specifications
 * and schematic/drawing paths using Room Database (ComponentSpecEntity & ComponentSpecDao).
 */
class ComponentSpecRepository private constructor(context: Context) {

    private val database = TavFurnaceDatabase.getDatabase(context)
    private val dao = database.componentSpecDao()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            seedInitialSpecsIfNeeded()
        }
    }

    /**
     * Pre-populates the Room database with technical specifications and schema paths
     * for all components of the TAV TPH 120/120/230 furnace if table is empty.
     */
    private suspend fun seedInitialSpecsIfNeeded() {
        if (dao.getCount() == 0) {
            val schematicsByCompId = ComponentSchematicsData.list.associateBy { it.componentId }
            val schematicsByTag = ComponentSchematicsData.list.associateBy { it.tag }

            val initialSpecs = ComponentsData.list.map { comp ->
                val schematic = schematicsByCompId[comp.id] ?: schematicsByTag[comp.tag]
                val dwgCode = schematic?.drawingCode ?: "DWG-21702-${comp.tag}"

                ComponentSpecEntity(
                    componentId = comp.id,
                    tag = comp.tag,
                    pidCode = comp.pidCode,
                    nameEn = comp.nameEn,
                    nameAr = comp.nameAr,
                    subsystem = comp.subsystem.name,
                    manufacturerModel = comp.manufacturerModel,
                    specifications = comp.specifications,
                    electricalCabinet = comp.electricalCabinet,
                    schematicsPage = comp.schematicsPage,
                    drawingCode = dwgCode,
                    drawingTitleEn = schematic?.drawingTitleEn ?: "${comp.nameEn} Schematic & Layout",
                    drawingTitleAr = schematic?.drawingTitleAr ?: "مخطط ورسم ${comp.nameAr}",
                    schematicDrawingType = schematic?.schematicType?.name ?: "ELECTRICAL_WIRING",
                    schemaFilePath = "schematics/$dwgCode.dwg",
                    terminalBlock = schematic?.terminalBlock ?: "Cabinet: ${comp.electricalCabinet}",
                    plcAddressTag = schematic?.plcAddressTag ?: "%I0.0",
                    mechanicalMounting = schematic?.mechanicalMounting ?: "Standard TAV Mounting Flange",
                    pneumaticOrFluidLine = schematic?.pneumaticOrFluidLine ?: "Process Connection DN Line",
                    interlockPermissiveAr = schematic?.interlockPermissiveAr ?: "شروط الأمان والقفل الكهربائي المعتمدة للمنظومة",
                    interlockPermissiveEn = schematic?.interlockPermissiveEn ?: "Safety interlock permissives active",
                    flowDirectionDescriptionAr = schematic?.flowDirectionDescriptionAr ?: "سريان المائع والغاز داخل دارة الفرن الفراغي",
                    flowDirectionDescriptionEn = schematic?.flowDirectionDescriptionEn ?: "Process fluid/gas flow through vacuum circuit",
                    circuitDomain = schematic?.circuitDomain?.name ?: "ELECTRICAL",
                    circuitNodeId = schematic?.circuitNodeId,
                    customNotes = "",
                    isVerified = true,
                    updatedAt = System.currentTimeMillis()
                )
            }

            dao.insertAll(initialSpecs)
        }
    }

    val allComponentSpecs: Flow<List<ComponentSpecEntity>> = dao.getAllComponentSpecs()

    fun getComponentSpecById(componentId: String): Flow<ComponentSpecEntity?> =
        dao.getComponentSpecById(componentId)

    suspend fun findComponentSpecById(componentId: String): ComponentSpecEntity? =
        withContext(Dispatchers.IO) {
            dao.findComponentSpecById(componentId)
        }

    suspend fun findComponentSpecByTag(tag: String): ComponentSpecEntity? =
        withContext(Dispatchers.IO) {
            dao.findComponentSpecByTag(tag)
        }

    fun getComponentSpecsBySubsystem(subsystem: String): Flow<List<ComponentSpecEntity>> =
        dao.getComponentSpecsBySubsystem(subsystem)

    fun searchComponentSpecs(query: String): Flow<List<ComponentSpecEntity>> =
        dao.searchComponentSpecs(query.trim())

    suspend fun saveComponentSpec(spec: ComponentSpecEntity): Long =
        withContext(Dispatchers.IO) {
            dao.insertComponentSpec(spec)
        }

    suspend fun updateSchemaPath(componentId: String, schemaPath: String) =
        withContext(Dispatchers.IO) {
            dao.updateSchemaPath(componentId, schemaPath)
        }

    suspend fun updateSpecifications(componentId: String, specifications: String) =
        withContext(Dispatchers.IO) {
            dao.updateSpecifications(componentId, specifications)
        }

    suspend fun updateCustomNotes(componentId: String, notes: String) =
        withContext(Dispatchers.IO) {
            dao.updateCustomNotes(componentId, notes)
        }

    suspend fun deleteComponentSpec(componentId: String) =
        withContext(Dispatchers.IO) {
            dao.deleteComponentSpecById(componentId)
        }

    companion object {
        @Volatile
        private var INSTANCE: ComponentSpecRepository? = null

        fun getInstance(context: Context): ComponentSpecRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = ComponentSpecRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
