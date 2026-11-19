package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FurnaceRepository
import com.example.data.SearchResultItem
import com.example.data.local.ComponentPhotoEntity
import com.example.data.local.ComponentPhotoRepository
import com.example.data.local.ManualDocumentEntity
import com.example.data.local.ManualRepository
import com.example.model.AlarmItem
import com.example.model.ComponentItem
import com.example.model.SubsystemCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NavigationScreen {
    HOME,
    SEARCH,
    ALARMS,
    COMPONENTS,
    MAINTENANCE,
    OPERATION,
    MANUAL,
    ABOUT,
    CIRCUITS,
    AI_CHAT,
    CALCULATORS,
    SHIFT_LOG,
    TELEMETRY,
    SCANNER,
    PRESENTATION,
    PREDICTIVE_HEALTH,
    QR_LABELS,
    PYROMETRY,
    VIBRATION_ANALYSIS,
    SAFETY_LOTO,
    RECIPE_DESIGNER,
    FAULT_TREE,
    SHIFT_HANDOVER,
    DATA_EXCHANGE
}

enum class AppLanguage(val code: String, val label: String, val badge: String) {
    ARABIC("ar", "العربية", "عربي"),
    ENGLISH("en", "English", "EN"),
    FRENCH("fr", "Français", "FR")
}

data class AppUiState(
    val currentScreen: NavigationScreen = NavigationScreen.HOME,
    val language: AppLanguage = AppLanguage.ARABIC,
    val searchQuery: String = "",
    val selectedCategory: SubsystemCategory = SubsystemCategory.ALL,
    val searchResults: List<SearchResultItem> = emptyList(),
    val selectedAlarm: AlarmItem? = null,
    val selectedComponent: ComponentItem? = null,
    val bookmarkedAlarms: Set<String> = emptySet(),
    val completedMaintenanceTasks: Set<String> = emptySet(),
    val isFurnaceActiveSimulated: Boolean = true,
    // Component Health Hours tracking
    val healthRunHours: Map<String, Int> = mapOf(
        "SV630B_OIL" to 840, // max 1500h
        "WAU2001_BEARINGS" to 2200, // max 6000h
        "DIP3000_FLUID" to 1100, // max 3000h
        "VAT_SEALS_CYCLES" to 3420, // max 10000 cycles
        "CFC_HOT_ZONE_HOURS" to 1280, // max 4000h
        "COOLING_FILTER_DAYS" to 45 // max 90 days
    ),
    // Offline Manual Room database state
    val manualSearchQuery: String = "",
    val selectedManualCategory: String = "ALL",
    val selectedManualChapter: String = "ALL",
    val selectedManualDoc: ManualDocumentEntity? = null,
    val showAboutDialog: Boolean = false,
    // Interactive Circuit Schematics state
    val selectedCircuitDomain: com.example.model.CircuitDomain = com.example.model.CircuitDomain.ELECTRICAL,
    val selectedCircuitNode: com.example.model.CircuitNode? = null,
    val activeTracePathId: String? = null,
    val isSimulationFlowActive: Boolean = true,
    // AI Chatbot Assistant State
    val chatMessages: List<com.example.model.ChatMessage> = emptyList(),
    val chatInputText: String = "",
    val isAiResponding: Boolean = false,
    val aiAssistantMode: com.example.model.AiAssistantMode = com.example.model.AiAssistantMode.GENERAL_DIAGNOSTICS,
    val customApiKey: String = "sk-JkRTC3ITIkhIB3aFS82uahpRtHNXw70V2wtaJHLNRhzpzAra",
    val showApiKeyDialog: Boolean = false
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val manualRepository = ManualRepository.getInstance(application)
    private val aiResponseCacheRepository = com.example.data.local.AiResponseCacheRepository.getInstance(application)
    private val componentPhotoRepository = ComponentPhotoRepository.getInstance(application)
    private val shiftLogRepository = com.example.data.local.ShiftLogRepository.getInstance(application)
    private val componentSpecRepository = com.example.data.local.ComponentSpecRepository.getInstance(application)
    private val geminiAiService = com.example.data.GeminiAiService()

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    // Room database shift logs flow
    val shiftLogs: StateFlow<List<com.example.data.local.ShiftLogEntity>> = shiftLogRepository
        .allShiftLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addShiftLog(log: com.example.data.local.ShiftLogEntity) {
        viewModelScope.launch {
            shiftLogRepository.insertShiftLog(log)
        }
    }

    fun importShiftLogs(logs: List<com.example.data.local.ShiftLogEntity>) {
        viewModelScope.launch {
            logs.forEach { log ->
                shiftLogRepository.insertShiftLog(log.copy(id = 0))
            }
        }
    }

    fun deleteShiftLog(log: com.example.data.local.ShiftLogEntity) {
        viewModelScope.launch {
            shiftLogRepository.deleteShiftLog(log)
        }
    }

    // Room database custom component photos flow (componentId -> ComponentPhotoEntity)
    val customComponentPhotos: StateFlow<Map<String, ComponentPhotoEntity>> = componentPhotoRepository
        .allPhotos
        .map { list -> list.associateBy { it.componentId } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    // Room database cached AI responses flow (for offline inspection)
    val cachedAiResponses: StateFlow<List<com.example.data.local.CachedAiResponseEntity>> = aiResponseCacheRepository
        .getAllCachedResponses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Room database component specifications & schema paths flow
    val componentSpecs: StateFlow<List<com.example.data.local.ComponentSpecEntity>> = componentSpecRepository
        .allComponentSpecs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun updateComponentSchemaPath(componentId: String, schemaPath: String) {
        viewModelScope.launch {
            componentSpecRepository.updateSchemaPath(componentId, schemaPath)
        }
    }

    fun updateComponentSpecifications(componentId: String, specifications: String) {
        viewModelScope.launch {
            componentSpecRepository.updateSpecifications(componentId, specifications)
        }
    }

    fun updateComponentCustomNotes(componentId: String, notes: String) {
        viewModelScope.launch {
            componentSpecRepository.updateCustomNotes(componentId, notes)
        }
    }

    // Room database flows
    private val _manualSearchQuery = MutableStateFlow("")

    val manualDocuments: StateFlow<List<ManualDocumentEntity>> = _manualSearchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                manualRepository.getAllDocuments()
            } else {
                manualRepository.searchManual(query)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val bookmarkedManualDocs: StateFlow<List<ManualDocumentEntity>> = manualRepository
        .getBookmarkedDocuments()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        val prefs = getApplication<Application>().getSharedPreferences("tav_furnace_prefs", android.content.Context.MODE_PRIVATE)
        val savedKey = prefs.getString("custom_api_key", "sk-JkRTC3ITIkhIB3aFS82uahpRtHNXw70V2wtaJHLNRhzpzAra") ?: "sk-JkRTC3ITIkhIB3aFS82uahpRtHNXw70V2wtaJHLNRhzpzAra"
        _uiState.update { it.copy(customApiKey = savedKey) }
        performSearch("", SubsystemCategory.ALL)
    }

    fun setCustomApiKey(key: String) {
        val trimmed = key.trim()
        val prefs = getApplication<Application>().getSharedPreferences("tav_furnace_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putString("custom_api_key", trimmed).apply()
        _uiState.update { it.copy(customApiKey = trimmed) }
    }

    fun openApiKeyDialog() {
        _uiState.update { it.copy(showApiKeyDialog = true) }
    }

    fun closeApiKeyDialog() {
        _uiState.update { it.copy(showApiKeyDialog = false) }
    }

    fun navigateTo(screen: NavigationScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun resetComponentHours(key: String) {
        _uiState.update {
            val newMap = it.healthRunHours.toMutableMap()
            newMap[key] = 0
            it.copy(healthRunHours = newMap)
        }
    }

    fun updateComponentRunHours(key: String, hours: Int) {
        _uiState.update {
            val newMap = it.healthRunHours.toMutableMap()
            newMap[key] = hours
            it.copy(healthRunHours = newMap)
        }
    }

    fun toggleLanguage() {
        _uiState.update {
            val next = when (it.language) {
                AppLanguage.ARABIC -> AppLanguage.FRENCH
                AppLanguage.FRENCH -> AppLanguage.ENGLISH
                AppLanguage.ENGLISH -> AppLanguage.ARABIC
            }
            it.copy(language = next)
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _uiState.update { it.copy(language = lang) }
    }

    fun onSearchQueryChanged(newQuery: String) {
        _uiState.update { it.copy(searchQuery = newQuery) }
        performSearch(newQuery, _uiState.value.selectedCategory)
    }

    fun onCategoryFilterChanged(category: SubsystemCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
        performSearch(_uiState.value.searchQuery, category)
    }

    fun quickSearch(term: String) {
        _uiState.update {
            it.copy(
                currentScreen = NavigationScreen.SEARCH,
                searchQuery = term,
                selectedCategory = SubsystemCategory.ALL
            )
        }
        performSearch(term, SubsystemCategory.ALL)
    }

    private fun performSearch(query: String, category: SubsystemCategory) {
        val results = FurnaceRepository.smartSearch(query, category)
        _uiState.update { it.copy(searchResults = results) }
    }

    fun selectAlarm(alarm: AlarmItem?) {
        _uiState.update { it.copy(selectedAlarm = alarm) }
    }

    fun selectComponent(component: ComponentItem?) {
        _uiState.update { it.copy(selectedComponent = component) }
    }

    fun toggleBookmarkAlarm(alarmId: String) {
        _uiState.update { current ->
            val set = current.bookmarkedAlarms.toMutableSet()
            if (set.contains(alarmId)) set.remove(alarmId) else set.add(alarmId)
            current.copy(bookmarkedAlarms = set)
        }
    }

    fun toggleMaintenanceTask(taskId: String) {
        _uiState.update { current ->
            val set = current.completedMaintenanceTasks.toMutableSet()
            if (set.contains(taskId)) set.remove(taskId) else set.add(taskId)
            current.copy(completedMaintenanceTasks = set)
        }
    }

    // Room Database Operations for Offline Manual
    fun onManualSearchQueryChanged(query: String) {
        _manualSearchQuery.value = query
        _uiState.update { it.copy(manualSearchQuery = query) }
    }

    fun filterManualByCategory(category: String) {
        _uiState.update { it.copy(selectedManualCategory = category) }
    }

    fun filterManualByChapter(chapter: String) {
        _uiState.update { it.copy(selectedManualChapter = chapter) }
    }

    fun selectManualDocument(doc: ManualDocumentEntity?) {
        _uiState.update { it.copy(selectedManualDoc = doc) }
    }

    fun toggleManualBookmark(id: String, currentStatus: Boolean) {
        viewModelScope.launch {
            manualRepository.toggleBookmark(id, !currentStatus)
        }
    }

    fun saveTechnicianNote(id: String, note: String) {
        viewModelScope.launch {
            manualRepository.updateTechnicianNote(id, note)
        }
    }

    fun resetManualDatabase() {
        viewModelScope.launch {
            manualRepository.resetToDefaultContent()
        }
    }

    fun openAboutDialog() {
        _uiState.update { it.copy(showAboutDialog = true) }
    }

    fun closeAboutDialog() {
        _uiState.update { it.copy(showAboutDialog = false) }
    }

    // Interactive Circuit Schematics Operations
    fun selectCircuitDomain(domain: com.example.model.CircuitDomain) {
        _uiState.update {
            it.copy(
                selectedCircuitDomain = domain,
                selectedCircuitNode = null,
                activeTracePathId = null
            )
        }
    }

    fun openCircuitForComponent(domain: com.example.model.CircuitDomain, nodeId: String?) {
        val targetNode = if (nodeId != null) {
            val diagram = com.example.data.CircuitsData.getDiagramByDomain(domain)
            diagram.nodes.find { it.id == nodeId }
        } else null
        _uiState.update {
            it.copy(
                selectedComponent = null,
                selectedCircuitDomain = domain,
                selectedCircuitNode = targetNode,
                currentScreen = NavigationScreen.CIRCUITS
            )
        }
    }

    fun selectCircuitNode(node: com.example.model.CircuitNode?) {
        _uiState.update { it.copy(selectedCircuitNode = node) }
    }

    fun toggleTracePath(pathId: String) {
        _uiState.update {
            it.copy(
                activeTracePathId = if (it.activeTracePathId == pathId) null else pathId
            )
        }
    }

    fun toggleCircuitSimulation() {
        _uiState.update {
            it.copy(isSimulationFlowActive = !it.isSimulationFlowActive)
        }
    }

    // AI Chatbot Operations
    fun updateChatInput(text: String) {
        _uiState.update { it.copy(chatInputText = text) }
    }

    fun setAiAssistantMode(mode: com.example.model.AiAssistantMode) {
        _uiState.update { it.copy(aiAssistantMode = mode) }
    }

    fun sendChatMessage() {
        val query = _uiState.value.chatInputText.trim()
        if (query.isBlank() || _uiState.value.isAiResponding) return

        val userMessage = com.example.model.ChatMessage(
            sender = com.example.model.ChatSender.USER,
            text = query
        )

        val updatedMessages = _uiState.value.chatMessages + userMessage
        val currentMode = _uiState.value.aiAssistantMode

        _uiState.update {
            it.copy(
                chatMessages = updatedMessages,
                chatInputText = "",
                isAiResponding = true
            )
        }

        viewModelScope.launch {
            val responseMsg = geminiAiService.sendMessage(
                history = updatedMessages,
                userText = query,
                mode = currentMode,
                customApiKey = _uiState.value.customApiKey
            )

            // Caching layer in Room database
            if (!responseMsg.isError && responseMsg.text.isNotBlank()) {
                aiResponseCacheRepository.cacheResponse(
                    query = query,
                    responseText = responseMsg.text,
                    mode = currentMode.name,
                    groundings = responseMsg.groundings,
                    generatedImageBase64 = responseMsg.generatedImageBase64
                )
            } else if (responseMsg.isError) {
                // If live call failed (e.g. offline or error), attempt to retrieve cached response
                val cached = aiResponseCacheRepository.getExactCachedResponse(query)
                if (cached != null) {
                    val offlineNotice = when (_uiState.value.language) {
                        AppLanguage.ARABIC -> "💾 [استجابة مسترجعة من الذاكرة المحلية (Offline Cache)]\n\n${cached.responseText}"
                        AppLanguage.FRENCH -> "💾 [Réponse récupérée de la mémoire cache locale (Offline Cache)]\n\n${cached.responseText}"
                        AppLanguage.ENGLISH -> "💾 [Retrieved from Local Offline Cache]\n\n${cached.responseText}"
                    }
                    val offlineMsg = com.example.model.ChatMessage(
                        sender = com.example.model.ChatSender.ASSISTANT,
                        text = offlineNotice,
                        generatedImageBase64 = cached.generatedImageBase64,
                        isError = false
                    )
                    _uiState.update {
                        it.copy(
                            chatMessages = it.chatMessages + offlineMsg,
                            isAiResponding = false
                        )
                    }
                    return@launch
                }
            }

            _uiState.update {
                it.copy(
                    chatMessages = it.chatMessages + responseMsg,
                    isAiResponding = false
                )
            }
        }
    }

    fun loadCachedResponseIntoChat(cached: com.example.data.local.CachedAiResponseEntity) {
        val userMsg = com.example.model.ChatMessage(
            sender = com.example.model.ChatSender.USER,
            text = cached.query
        )
        val offlineNotice = when (_uiState.value.language) {
            AppLanguage.ARABIC -> "💾 [محفوظة في الذاكرة المحلية - Offline]\n\n${cached.responseText}"
            AppLanguage.FRENCH -> "💾 [Enregistré en cache local - Offline]\n\n${cached.responseText}"
            AppLanguage.ENGLISH -> "💾 [Cached Offline Response]\n\n${cached.responseText}"
        }
        val assistantMsg = com.example.model.ChatMessage(
            sender = com.example.model.ChatSender.ASSISTANT,
            text = offlineNotice,
            generatedImageBase64 = cached.generatedImageBase64,
            isError = false
        )
        _uiState.update {
            it.copy(
                chatMessages = it.chatMessages + userMsg + assistantMsg
            )
        }
    }

    fun clearAiCache() {
        viewModelScope.launch {
            aiResponseCacheRepository.clearCache()
        }
    }

    fun deleteCachedAiResponse(id: String) {
        viewModelScope.launch {
            aiResponseCacheRepository.deleteCachedResponse(id)
        }
    }

    fun askAiAboutAlarm(alarm: com.example.model.AlarmItem) {
        val prompt = when (_uiState.value.language) {
            AppLanguage.ARABIC -> "اشرح لي سبب الإنذار ${alarm.id} (${alarm.nameAr}) في فرن TAV TPH 120 والخطوات الفنية التتابعية التي يجب عليّ كفني اتخاذها لإصلاحه بأمان ومراجعة القواطع المعنية."
            AppLanguage.FRENCH -> "Expliquez la cause technique de l'alarme ${alarm.id} (${alarm.nameEn}) du four TAV TPH 120 et décrivez les étapes de dépannage, de réarmement des disjoncteurs et les consignes de sécurité LOTO."
            AppLanguage.ENGLISH -> "Explain the technical cause of alarm ${alarm.id} (${alarm.nameEn}) in TAV furnace TPH 120 and list step-by-step diagnostic and reset procedures, including relevant circuit breakers and safety interlocks."
        }

        _uiState.update {
            it.copy(
                currentScreen = NavigationScreen.AI_CHAT,
                chatInputText = prompt
            )
        }
        sendChatMessage()
    }

    fun clearChatHistory() {
        _uiState.update { it.copy(chatMessages = emptyList()) }
    }

    fun saveComponentPhoto(componentId: String, uri: Uri, notes: String = "") {
        viewModelScope.launch {
            componentPhotoRepository.savePhotoFromUri(componentId, uri, notes)
        }
    }

    fun deleteComponentPhoto(componentId: String) {
        viewModelScope.launch {
            componentPhotoRepository.deletePhoto(componentId)
        }
    }
}
