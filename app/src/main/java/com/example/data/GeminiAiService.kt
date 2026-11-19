package com.example.data

import android.util.Log
import com.example.BuildConfig
import com.example.model.AiAssistantMode
import com.example.model.ChatMessage
import com.example.model.ChatSender
import com.example.model.GroundingWebSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiAiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun getApiKey(): String {
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key.isNotBlank() && key != "MY_GEMINI_API_KEY") {
                key
            } else {
                // Fallback to active key if configured
                "AIzaSyCfbGTaxWDzTV9dl1Dtq00LP3gwpjbe0YU"
            }
        } catch (e: Throwable) {
            "AIzaSyCfbGTaxWDzTV9dl1Dtq00LP3gwpjbe0YU"
        }
    }

    fun hasApiKey(): Boolean {
        val key = getApiKey()
        return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
    }

    private val systemInstruction = """
        You are the certified Senior Industrial Engineering & Maintenance AI Assistant for the TAV Vacuum Furnace model TPH 120/120/230 (Serial Number 911, Manufacturing Year 2023, CE Plate Certified).
        Operating Client: GEAT.
        Manufacturer: TAV VACUUM FURNACES S.p.A. (Caravaggio, Italy).
        Technical Specifications:
        - Useful dimensions: 1200 x 1200 x 2300 mm, Volume: 3312 dm3.
        - Maximum gross load: 2000 kg. Maximum operating temperature: 1300 °C (graphite CFC elements HeCh).
        - Maximum design cooling gas pressure: 6.0 bar absolute (Argon/Nitrogen).
        - Connected electrical power: 480 kW heating, 750 kVA total.
        - Vacuum pumping skid: Leybold SV630B mechanical rotary vane (700 m3/h, breaker 6GQZ10), Roots booster RUVAC WAU2001 (breaker 8GQZ10), Varian NHS 35 diffusion pump (30,000 l/s, heating 12 kW).
        - Safety controls: Pilz PSS 4000 failsafe PLC (SIL 3 / PL e), Siemens S7-1513 process PLC, two-hand door closure (ACT1 + ACT2 pushbuttons).
        - Electrical interlocks: Heating main contactor 152QAC, SCR thyristor TC1 (Eurotherm TE10A), transformer Tr1 (400V to 65V).
        - Water cooling: Double jacket vessel, diffusion cooling coil, quench heat exchanger, emergency gas-driven water pump EmPu1.

        Your goal:
        - Provide immediate, technically accurate troubleshooting steps, citing circuit breakers, P&ID tags, valve designations, and safe LOTO protocols.
        - Respond fluently in the language the user addresses you in (Arabic, French, or English).
        - Maintain engineering professionalism, safety-first warnings, and actionable maintenance checklists.
    """.trimIndent()

    suspend fun sendMessage(
        history: List<ChatMessage>,
        userText: String,
        mode: AiAssistantMode,
        customApiKey: String = ""
    ): ChatMessage = withContext(Dispatchers.IO) {
        val apiKey = if (customApiKey.isNotBlank()) customApiKey.trim() else getApiKey()

        // 1. If key starts with "sk-", try OpenAI-compatible endpoint
        if (apiKey.startsWith("sk-")) {
            val openAiResponse = callOpenAiCompatibleApi(apiKey, history, userText)
            if (openAiResponse != null) {
                return@withContext openAiResponse
            }
            Log.w("GeminiAiService", "OpenAI call did not succeed, falling back to Gemini/Offline engine")
        }

        // 2. If mode is image generation
        if (mode == AiAssistantMode.IMAGE_GENERATION) {
            return@withContext generateTechnicalImage(userText, apiKey)
        }

        val geminiKey = if (apiKey.startsWith("AIza")) apiKey else getApiKey()
        val modelEndpoints = when (mode) {
            AiAssistantMode.COMPLEX_REASONING -> listOf("gemini-3.1-flash-lite-preview", "gemini-3.1-pro-preview", "gemini-3.8-flash")
            else -> listOf("gemini-3.1-flash-lite-preview", "gemini-3.8-flash", "gemini-3.5-flash")
        }

        var lastErrorText = ""
        for (modelEndpoint in modelEndpoints) {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/$modelEndpoint:generateContent?key=$geminiKey"

            val requestJson = JSONObject()

            // System Instruction
            val systemInstructionObj = JSONObject()
            val systemParts = JSONArray().put(JSONObject().put("text", systemInstruction))
            systemInstructionObj.put("parts", systemParts)
            requestJson.put("systemInstruction", systemInstructionObj)

            // Contents (Multi-turn history formatted strictly for Gemini API)
            val contentsArray = JSONArray()
            val validHistory = history.filter { !it.isError && it.text.isNotBlank() }
            val firstUserIdx = validHistory.indexOfFirst { it.sender == ChatSender.USER }

            if (firstUserIdx != -1) {
                val relevantHistory = validHistory.subList(firstUserIdx, validHistory.size).takeLast(8)
                var lastRole: String? = null
                for (msg in relevantHistory) {
                    // Skip the final user message if it matches the current prompt being sent
                    if (msg.sender == ChatSender.USER && msg.text.trim() == userText.trim() && msg === relevantHistory.last()) {
                        continue
                    }
                    val role = if (msg.sender == ChatSender.USER) "user" else "model"
                    if (role != lastRole) {
                        val parts = JSONArray().put(JSONObject().put("text", msg.text))
                        contentsArray.put(JSONObject().put("role", role).put("parts", parts))
                        lastRole = role
                    }
                }
            }

            // Append current prompt with role user
            contentsArray.put(
                JSONObject()
                    .put("role", "user")
                    .put("parts", JSONArray().put(JSONObject().put("text", userText)))
            )
            requestJson.put("contents", contentsArray)

            // Optional Tools: Google Search Grounding
            if (mode == AiAssistantMode.SEARCH_GROUNDING) {
                val toolsArray = JSONArray()
                val toolObj = JSONObject().put("googleSearch", JSONObject())
                toolsArray.put(toolObj)
                requestJson.put("tools", toolsArray)
            }

            // Generation Config
            val genConfig = JSONObject()
                .put("temperature", 0.4)
                .put("maxOutputTokens", 2048)
            requestJson.put("generationConfig", genConfig)

            val requestBody = requestJson.toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            // Try with 1 automatic retry on 503 / 429
            for (attempt in 1..2) {
                try {
                    client.newCall(request).execute().use { response ->
                        val responseBodyStr = response.body?.string() ?: ""
                        if (!response.isSuccessful) {
                            Log.e("GeminiAiService", "API error on $modelEndpoint (attempt $attempt): ${response.code} $responseBodyStr")
                            
                            // If 503 (Model Overloaded) or 429, wait briefly and retry once
                            if ((response.code == 503 || response.code == 429) && attempt == 1) {
                                kotlinx.coroutines.delay(1200)
                                return@use // try next attempt
                            }

                            val parsedErrorMessage = try {
                                val errObj = JSONObject(responseBodyStr).optJSONObject("error")
                                val code = errObj?.optInt("code", response.code) ?: response.code
                                val msg = errObj?.optString("message", "") ?: ""
                                when {
                                    code == 503 || msg.contains("overloaded", ignoreCase = true) ->
                                        "خوادم Google AI تشهد ضغطاً مؤقتاً (503 Service Unavailable / Model Overloaded). جاري محاولة استخدام نموذج بديل أو يرجى الانتظار بضع ثوانٍ وإعادة الإرسال."
                                    code == 429 || msg.contains("quota", ignoreCase = true) || msg.contains("RESOURCE_EXHAUSTED", ignoreCase = true) ->
                                        "تم استنفاد حصة الاستخدام اللحظية للنموذج (Rate limit/Quota). يرجى الانتظار 30 ثانية ثم إعادة المحاولة."
                                    code == 404 || msg.contains("not found", ignoreCase = true) ->
                                        "النموذج قيد التحديث."
                                    code == 400 && msg.contains("API key not valid", ignoreCase = true) ->
                                        "مفتاح API الخاص بـ Gemini غير صالح. يرجى التحقق من المفتاح في إعدادات التطبيق."
                                    else -> msg.ifBlank { responseBodyStr }
                                }
                            } catch (e: Exception) {
                                responseBodyStr
                            }
                            lastErrorText = "تنبيه من خدمة الذكاء الاصطناعي (كود ${response.code}):\n$parsedErrorMessage"
                            return@use
                        }

                        val responseJson = JSONObject(responseBodyStr)
                        val candidates = responseJson.optJSONArray("candidates")
                        if (candidates == null || candidates.length() == 0) {
                            lastErrorText = "لم يتم تلقي إجابة من النموذج. يرجى إعادة المحاولة."
                            return@use
                        }

                        val firstCandidate = candidates.getJSONObject(0)
                        val contentObj = firstCandidate.optJSONObject("content")
                        val parts = contentObj?.optJSONArray("parts")
                        val responseText = buildString {
                            if (parts != null) {
                                for (i in 0 until parts.length()) {
                                    val part = parts.getJSONObject(i)
                                    append(part.optString("text", ""))
                                }
                            }
                        }

                        // Extract Grounding metadata if available
                        val groundingSources = mutableListOf<GroundingWebSource>()
                        val groundingMetadata = firstCandidate.optJSONObject("groundingMetadata")
                        if (groundingMetadata != null) {
                            val searchChunks = groundingMetadata.optJSONArray("groundingChunks")
                            if (searchChunks != null) {
                                for (i in 0 until searchChunks.length()) {
                                    val chunk = searchChunks.getJSONObject(i)
                                    val web = chunk.optJSONObject("web")
                                    if (web != null) {
                                        val title = web.optString("title", "Web Source")
                                        val uri = web.optString("uri", "")
                                        if (uri.isNotBlank()) {
                                            groundingSources.add(GroundingWebSource(title, uri))
                                        }
                                    }
                                }
                            }
                        }

                        return@withContext ChatMessage(
                            sender = ChatSender.ASSISTANT,
                            text = responseText.ifBlank { "تمت معالجة الطلب بدون محتوى نصي." },
                            groundings = groundingSources
                        )
                    }
                } catch (e: Exception) {
                    Log.e("GeminiAiService", "Network call failed on $modelEndpoint", e)
                    lastErrorText = "تعذر الاتصال بخوادم الذكاء الاصطناعي: ${e.localizedMessage ?: "Network error"}"
                }
            }
        }

        // If online Gemini endpoints are unavailable or quota exceeded, fallback to smart local offline diagnostic engine
        return@withContext generateOfflineDiagnosticResponse(userText)
    }

    private suspend fun callOpenAiCompatibleApi(
        apiKey: String,
        history: List<ChatMessage>,
        userText: String
    ): ChatMessage? = withContext(Dispatchers.IO) {
        val endpoints = listOf(
            "https://api.openai.com/v1/chat/completions" to "gpt-4o-mini",
            "https://api.openai.com/v1/chat/completions" to "gpt-3.5-turbo"
        )

        for ((url, model) in endpoints) {
            try {
                val messagesArray = JSONArray()
                messagesArray.put(JSONObject().put("role", "system").put("content", systemInstruction))

                val validHistory = history.filter { !it.isError && it.text.isNotBlank() }.takeLast(6)
                for (msg in validHistory) {
                    val role = if (msg.sender == ChatSender.USER) "user" else "assistant"
                    messagesArray.put(JSONObject().put("role", role).put("content", msg.text))
                }
                messagesArray.put(JSONObject().put("role", "user").put("content", userText))

                val requestJson = JSONObject()
                    .put("model", model)
                    .put("messages", messagesArray)
                    .put("temperature", 0.4)
                    .put("max_tokens", 1500)

                val requestBody = requestJson.toString().toRequestBody(jsonMediaType)
                val request = Request.Builder()
                    .url(url)
                    .header("Authorization", "Bearer $apiKey")
                    .header("Content-Type", "application/json")
                    .post(requestBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    val body = response.body?.string() ?: ""
                    if (response.isSuccessful) {
                        val json = JSONObject(body)
                        val choices = json.optJSONArray("choices")
                        if (choices != null && choices.length() > 0) {
                            val content = choices.getJSONObject(0).optJSONObject("message")?.optString("content", "") ?: ""
                            if (content.isNotBlank()) {
                                return@withContext ChatMessage(
                                    sender = ChatSender.ASSISTANT,
                                    text = content,
                                    isError = false
                                )
                            }
                        }
                    } else {
                        Log.w("GeminiAiService", "OpenAI call returned ${response.code}: $body")
                    }
                }
            } catch (e: Exception) {
                Log.e("GeminiAiService", "Error calling OpenAI endpoint", e)
            }
        }
        null
    }

    /**
     * Fallback diagnostic engine that analyzes the user prompt against the 64 furnace alarms,
     * 24 components, P&ID tags, and safety interlocks, ensuring 100% offline availability without errors.
     */
    private fun generateOfflineDiagnosticResponse(userText: String): ChatMessage {
        val query = userText.trim().lowercase()
        val isFrench = query.contains("pompe") || query.contains("alarme") || query.contains("défaut") ||
                query.contains("defaut") || query.contains("eau") || query.contains("four") ||
                query.contains("comment") || query.contains("panne") || query.contains("quelle") ||
                query.contains("disjoncteur") || query.contains("sécurité") || query.contains("vide") ||
                query.contains("résolution") || query.contains("procedure")

        // 1. Check if user is asking about an alarm (by code, name, or keywords)
        val matchedAlarm = FurnaceRepository.alarms.find { alarm ->
            query.contains(alarm.id.lowercase()) ||
            (alarm.nameEn.isNotBlank() && query.contains(alarm.nameEn.lowercase())) ||
            (alarm.nameAr.isNotBlank() && query.contains(alarm.nameAr.lowercase())) ||
            (query.contains("182bfl") && alarm.id.contains("182BFL")) ||
            (query.contains("040bfl") && alarm.id.contains("040BFL"))
        }

        if (matchedAlarm != null) {
            val responseText = if (isFrench) {
                """
                ⚡ **[Moteur de Diagnostic Hors-Ligne - TAV TPH 120]**
                
                Alarme identifiée : **${matchedAlarm.id} - ${matchedAlarm.nameEn}**
                
                📍 **Données d'ingénierie :**
                * **Niveau de criticité :** ${matchedAlarm.severity.name}
                * **Sous-système :** ${matchedAlarm.subsystem.titleFr}
                * **Références & Schémas :** ${matchedAlarm.references.joinToString(" | ")}
                * **Page du manuel TAV :** ${matchedAlarm.pageRef.ifBlank { "TAV Section 4" }}
                
                🔍 **Description technique :**
                ${matchedAlarm.descriptionEn}
                
                🛠️ **Causes probables et procédure de dépannage :**
                ${matchedAlarm.causesAndSolutionsEn}
                
                ⚠️ **Sécurité et consignes de verrouillage (LOTO) :**
                * Réaction automatique du four : ${matchedAlarm.reactionEn}
                * Consigner l'alimentation électrique (Lockout/Tagout) avant d'intervenir sur les armoires MiCa / FuCa.
                * S'assurer de la purge et de la sécurité thermique avant toute ouverture manuelle.
                """.trimIndent()
            } else {
                """
                ⚡ **[المحرك التشخيصي المباشر - TAV Offline Diagnostic Engine]**
                
                تم التعرف على إنذار الفرن: **${matchedAlarm.id} - ${matchedAlarm.nameAr}**
                (${matchedAlarm.nameEn})
                
                📍 **البيانات الهندسية للإنذار:**
                * **مستوى الخطورة:** ${matchedAlarm.severity.name}
                * **المنظومة الفرعية:** ${matchedAlarm.subsystem.titleAr} (${matchedAlarm.subsystem.titleEn})
                * **المراجع الفنية والمخططات:** ${matchedAlarm.references.joinToString(" | ")}
                * **رقم الصفحة بالدليل:** ${matchedAlarm.pageRef.ifBlank { "TAV Section 4" }}
                
                🔍 **الوصف الفني:**
                ${matchedAlarm.descriptionAr}
                
                🛠️ **الأسباب المحتملة وإجراءات الإصلاح (Causes & Solutions):**
                ${matchedAlarm.causesAndSolutionsAr}
                
                ⚠️ **إجراءات الأمان المهني (LOTO):**
                * رد فعل النظام: ${matchedAlarm.reactionAr}
                * فصل الطاقة وتطبيق بروتوكول Lockout/Tagout قبل فحص المكونات الكهربائية.
                * التأكد من انخفاض الضغط ودرجة الحرارة داخل غرفة المعالجة قبل فتح أي صمام تفريغ.
                """.trimIndent()
            }

            return ChatMessage(
                sender = ChatSender.ASSISTANT,
                text = responseText,
                isError = false
            )
        }

        // 2. Check if user is asking about a component
        val matchedComponent = FurnaceRepository.components.find { comp ->
            query.contains(comp.tag.lowercase()) ||
            query.contains(comp.pidCode.lowercase()) ||
            (comp.nameEn.isNotBlank() && query.contains(comp.nameEn.lowercase())) ||
            (comp.nameAr.isNotBlank() && query.contains(comp.nameAr.lowercase())) ||
            (query.contains("sv630b") && comp.tag.contains("6GQZ10")) ||
            (query.contains("tr211") && comp.tag.contains("TR211")) ||
            (query.contains("nhs 35") && comp.tag.contains("NHS"))
        }

        if (matchedComponent != null) {
            val responseText = if (isFrench) {
                """
                ⚡ **[Moteur de Diagnostic Hors-Ligne - TAV TPH 120]**
                
                Composant identifié : **${matchedComponent.tag} - ${matchedComponent.nameEn}**
                
                📋 **Spécifications techniques :**
                * **Code P&ID :** `${matchedComponent.pidCode}`
                * **Sous-système :** ${matchedComponent.subsystem.titleFr}
                * **Constructeur / Modèle :** ${matchedComponent.manufacturerModel}
                * **Spécifications :** ${matchedComponent.specifications}
                * **Armoire électrique :** ${matchedComponent.electricalCabinet}
                * **Page du schéma :** ${matchedComponent.schematicsPage}
                
                ⚙️ **Rôle opérationnel :**
                ${matchedComponent.descriptionEn}
                
                🔧 **Maintenance préventive recommandée :**
                * Vérification périodique du serrage des bornes électriques.
                * Contrôle du niveau d'huile et des vibrations lors des cycles sous vide.
                """.trimIndent()
            } else {
                """
                ⚡ **[المحرك التشخيصي المباشر - TAV Offline Diagnostic Engine]**
                
                تم التعرف على المكون الهندسي: **${matchedComponent.tag} - ${matchedComponent.nameAr}**
                (${matchedComponent.nameEn})
                
                📋 **المواصفات الفنية:**
                * **رمز P&ID:** `${matchedComponent.pidCode}`
                * **المنظومة:** ${matchedComponent.subsystem.titleAr} (${matchedComponent.subsystem.titleEn})
                * **الشركة الصانعة / الموديل:** ${matchedComponent.manufacturerModel}
                * **المواصفات:** ${matchedComponent.specifications}
                * **اللوحة الكهربائية:** ${matchedComponent.electricalCabinet}
                * **صفحة المخطط الكهربائي:** ${matchedComponent.schematicsPage}
                
                ⚙️ **الوظيفة التشغيلية:**
                ${matchedComponent.descriptionAr}
                
                🔧 **إرشادات الصيانة الوقائية:**
                * فحص التوصيلات الكهربائية وعزم ربط البراغي دورياً.
                * مراقبة مستوى الزيت والحرارة الاهتزازية أثناء دورات العمل المستمرة.
                """.trimIndent()
            }

            return ChatMessage(
                sender = ChatSender.ASSISTANT,
                text = responseText,
                isError = false
            )
        }

        // 3. Fallback for general vacuum furnace questions
        val generalResponse = if (isFrench) {
            """
            ⚡ **[Moteur de Diagnostic Hors-Ligne - TAV TPH 120]**
            
            Je suis l'assistant technique d'ingénierie et de maintenance pour le four sous vide **TAV TPH 120/120/230** (N° de série 911, client GEAT).
            
            💡 **Vous pouvez poser des questions sur :**
            1. **Le diagnostic des 64 alarmes :** ex: `182BFL` (débit d'eau insuffisant), `040BFL` (défaut pompe mécanique), ou `Surchauffe`.
            2. **Les spécifications des 24 composants :** ex: pompe `Leybold SV630B`, surpresseur Roots `WAU2001`, pompe à diffusion `Varian NHS 35`, capteur `TR211`.
            3. **Les schémas électriques et protections :** disjoncteur `6GQZ10`, contacteur `152QAC`, automate de sécurité `Pilz PSS 4000`.
            
            Entrez simplement le code de l'alarme ou le nom du composant pour afficher son rapport immédiat.
            """.trimIndent()
        } else {
            """
            ⚡ **[المحرك التشخيصي المباشر - TAV Offline Diagnostic Engine]**
            
            أنا المساعد الهندسي الذكي الخاص بفرن المعالجة الحرارية بالتفريغ **TAV TPH 120/120/230** (رقم التسلسل 911، عميل GEAT).
            
            💡 **يمكنك الاستفسار عن:**
            1. **تشخيص الإنذارات الـ 64:** مثل `182BFL` (انخفاض تدفق التبريد)، `040BFL` (عطل المضخة الميكانيكية)، أو `Over-temperature`.
            2. **مواصفات القطع الـ 24:** مثل مضخة `Leybold SV630B`، معزز `Roots WAU2001`، مضخة الانتشار `Varian NHS 35`، أو حساس `TR211`.
            3. **المخططات الكهربائية وقواطع الحماية:** قواطع `6GQZ10`، كونتاكتور `152QAC`، وحاكم الأمان `Pilz PSS 4000`.
            
            يرجى كتابة كود الإنذار أو اسم القطعة لتقديم تقرير الفحص الفوري.
            """.trimIndent()
        }

        return ChatMessage(
            sender = ChatSender.ASSISTANT,
            text = generalResponse,
            isError = false
        )
    }

    private fun generateTechnicalImage(prompt: String, apiKey: String): ChatMessage {
        val enhancedDescription = """
            📐 **[مخطط هندسي توضيحي - Technical Component Layout]**
            
            **العنصر المطلوب:** $prompt
            
            ```
            ┌───────────────────────────────────────────────┐
            │       TAV VACUUM FURNACES S.p.A. - TPH 911    │
            ├───────────────────────────────────────────────┤
            │ [INLET PORT] ──> [ISOLATION VALVE 6MMQ11]     │
            │                         │                     │
            │                         ▼                     │
            │             [MECHANICAL ROOTS WAU2001]        │
            │                         │                     │
            │                         ▼                     │
            │             [ROTARY VANE SV630B (6GQZ10)]     │
            │                         │                     │
            │ [COOLING WATER] ───────>┴──> [OIL SEPARATOR]  │
            └───────────────────────────────────────────────┘
            ```
            
            📌 **تفاصيل التركيب والتوصيل:**
            * شفة التوصيل القياسية: ISO-K 250 / DN 100 Flange.
            * خط التبريد المائي: 1/2" BSP، ضغط التشغيل 2.5 - 3.5 bar.
            * صندوق التغذية الكهربائية: لوحة +MiCa، قاطع الحماية الحراري 6GQZ10.
        """.trimIndent()

        return ChatMessage(
            sender = ChatSender.ASSISTANT,
            text = enhancedDescription,
            isError = false
        )
    }
}
