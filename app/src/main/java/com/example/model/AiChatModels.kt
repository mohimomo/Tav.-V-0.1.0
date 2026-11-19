package com.example.model

enum class ChatSender {
    USER,
    ASSISTANT
}

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: ChatSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val groundings: List<GroundingWebSource> = emptyList(),
    val generatedImageBase64: String? = null,
    val isError: Boolean = false
)

data class GroundingWebSource(
    val title: String,
    val uri: String
)

enum class AiAssistantMode(val titleEn: String, val titleAr: String, val modelName: String) {
    GENERAL_DIAGNOSTICS("Diagnostics (Fast Flash)", "تشخيص الأعطال السريع", "gemini-3.8-flash"),
    COMPLEX_REASONING("Deep Engineering (Pro)", "التحليل الهندسي المعمق", "gemini-3.1-pro-preview"),
    SEARCH_GROUNDING("Search Grounding (Web)", "بحث الويب ومعايير السلامة", "gemini-3.8-flash"),
    IMAGE_GENERATION("Diagram & Visuals (Image)", "توليد الرسوم التوضيحية", "imagen-3.0-generate-002")
}
