package com.example.ui.screens

import android.content.Intent
import android.graphics.BitmapFactory
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AiAssistantMode
import com.example.model.ChatMessage
import com.example.model.ChatSender
import com.example.model.GroundingWebSource
import com.example.ui.AppLanguage
import com.example.ui.AppUiState
import com.example.ui.AppViewModel
import com.example.ui.NavigationScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.FurnaceAmber
import com.example.ui.theme.StatusGreen

@Composable
fun AiChatScreen(
    uiState: AppUiState,
    viewModel: AppViewModel,
    contentPadding: PaddingValues
) {
    BackHandler {
        viewModel.navigateTo(NavigationScreen.HOME)
    }

    val isAr = uiState.language == AppLanguage.ARABIC
    val context = androidx.compose.ui.platform.LocalContext.current
    val listState = rememberLazyListState()
    val cachedResponses by viewModel.cachedAiResponses.collectAsState()
    var showCacheHistoryDialog by remember { mutableStateOf(false) }

    // Text To Speech
    var tts: TextToSpeech? by remember { mutableStateOf(null) }
    var speakingText by remember { mutableStateOf<String?>(null) }

    androidx.compose.runtime.DisposableEffect(context) {
        val speech = TextToSpeech(context) { status ->
            // Initialized
        }
        tts = speech
        onDispose {
            speech.stop()
            speech.shutdown()
        }
    }

    // Voice Input Launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK && result.data != null) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spoken.isNullOrBlank()) {
                viewModel.updateChatInput(spoken)
            }
        }
    }

    // Visual Inspection Photo Picker Launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val visualPrompt = if (isAr)
                "[تم إرفاق صورة ميدانية لفحص العطل] ما هو تشخيص هذه القطعة أو الإنذار الموضح؟"
            else
                "[Attached field photo for visual inspection] What is the diagnosis for this component or alarm?"
            viewModel.updateChatInput(visualPrompt)
        }
    }

    // Scroll to bottom on new messages
    LaunchedEffect(uiState.chatMessages.size, uiState.isAiResponding) {
        if (uiState.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.chatMessages.size - 1)
        }
    }

    if (showCacheHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showCacheHistoryDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = CyanPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "سجل الاستجابات المحفوظة (Offline Cache)" else "Cached AI Responses (Offline)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                if (cachedResponses.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isAr) "لا توجد استجابات مخزنة محلياً بعد.\nكل استجابة ناجحة من الذكاء الاصطناعي تُحفظ تلقائياً في قاعدة بيانات Room لتعمل أوفلاين."
                            else "No cached responses yet.\nEvery successful AI answer is stored in Room database for offline access.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(350.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(cachedResponses) { cached ->
                            val dateStr = remember(cached.timestamp) {
                                SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date(cached.timestamp))
                            }
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.loadCachedResponseIntoChat(cached)
                                        showCacheHistoryDialog = false
                                    }
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = cached.query,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = CyanPrimary,
                                            maxLines = 1
                                        )
                                        IconButton(
                                            onClick = { viewModel.deleteCachedAiResponse(cached.id) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Delete",
                                                tint = AlertRed.copy(alpha = 0.7f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = cached.responseText,
                                        maxLines = 3,
                                        fontSize = 11.sp,
                                        lineHeight = 16.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Mode: ${cached.mode}",
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = dateStr,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCacheHistoryDialog = false }) {
                    Text(if (isAr) "إغلاق" else "Close")
                }
            },
            dismissButton = {
                if (cachedResponses.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearAiCache() }) {
                        Text(
                            text = if (isAr) "مسح السجل" else "Clear All",
                            color = AlertRed
                        )
                    }
                }
            }
        )
    }

    if (uiState.showApiKeyDialog) {
        var tempKey by remember { mutableStateOf(uiState.customApiKey) }
        AlertDialog(
            onDismissRequest = { viewModel.closeApiKeyDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = FurnaceAmber,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isAr) "إعدادات مفتاح الـ API" else "AI API Key Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (isAr)
                            "تم دمج وتفعيل مفتاح الـ API في المساعد الذكي بنجاح. يمكنك تعديل المفتاح أو إدخال مفتاح جديد أدناه:"
                        else
                            "The API key is connected to the furnace smart assistant. You can update or change it below:",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = tempKey,
                        onValueChange = { tempKey = it },
                        modifier = Modifier.fillMaxWidth().testTag("api_key_input"),
                        label = { Text("API Key (sk-... / AIza...)") },
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                    )

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(StatusGreen)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (tempKey.startsWith("sk-")) "نوع المفتاح المكتشف: OpenAI / LLM Bearer Key" else "نوع المفتاح: Google Gemini Key",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusGreen
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.setCustomApiKey(tempKey)
                        viewModel.closeApiKeyDialog()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyanPrimary)
                ) {
                    Text(if (isAr) "حفظ وتفعيل المفتاح" else "Save & Apply", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.closeApiKeyDialog() }) {
                    Text(if (isAr) "إلغاء" else "Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .testTag("ai_chat_screen")
    ) {
        // 1. Top Header Banner
        Card(
            shape = RoundedCornerShape(0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyanPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isAr) "مساعد الذكاء الاصطناعي لفرن TAV (Gemini)" else "TAV Gemini Industrial AI Assistant",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Model: ${uiState.aiAssistantMode.modelName} • S/N 911 Spec",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.openApiKeyDialog() },
                            modifier = Modifier.testTag("ai_api_key_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = if (isAr) "إعدادات مفتاح الـ API" else "API Key Settings",
                                tint = FurnaceAmber
                            )
                        }

                        IconButton(
                            onClick = { showCacheHistoryDialog = true },
                            modifier = Modifier.testTag("ai_cache_history_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = if (isAr) "سجل الردود المحفوظة أوفلاين" else "Offline Cache History",
                                tint = if (cachedResponses.isNotEmpty()) CyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (uiState.chatMessages.isNotEmpty()) {
                            IconButton(
                                onClick = { viewModel.clearChatHistory() },
                                modifier = Modifier.testTag("clear_chat_history_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear History",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mode Selector Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(AiAssistantMode.values()) { mode ->
                        val isSelected = uiState.aiAssistantMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setAiAssistantMode(mode) },
                            label = {
                                Text(
                                    text = if (isAr) mode.titleAr else mode.titleEn,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (mode) {
                                        AiAssistantMode.GENERAL_DIAGNOSTICS -> Icons.Default.Psychology
                                        AiAssistantMode.COMPLEX_REASONING -> Icons.Default.Build
                                        AiAssistantMode.SEARCH_GROUNDING -> Icons.Default.Search
                                        AiAssistantMode.IMAGE_GENERATION -> Icons.Default.Settings
                                    },
                                    contentDescription = null,
                                    tint = if (isSelected) CyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanPrimary.copy(alpha = 0.2f),
                                selectedLabelColor = CyanPrimary
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("chat_mode_${mode.name}")
                        )
                    }
                }
            }
        }

        // 2. Chat Conversation Thread
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            if (uiState.chatMessages.isEmpty()) {
                // Empty State with Starter Prompts
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 18.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(CyanPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isAr) "مرحباً بك في المساعد الذكي لفرن TAV TPH 120" else "Welcome to TAV Furnace AI Assistant",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isAr)
                            "اسأل عن خطوات تشخيص الأعطال، مواصفات قواطع الكهرباء، دورات التبريد، أو توليد مخططات هندسية."
                        else
                            "Ask diagnostic procedures, circuit breaker ratings, cooling loops, or generate technical diagrams.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = FurnaceAmber.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, FurnaceAmber.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clickable { viewModel.openApiKeyDialog() }
                            .testTag("ai_active_key_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = FurnaceAmber,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Active API Key: ${uiState.customApiKey.take(10)}...${uiState.customApiKey.takeLast(4)}",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = FurnaceAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Starter Prompt Buttons
                    val starterPrompts = if (isAr) listOf(
                        "كيف أعالج إنذار انقطاع مياه التبريد 182BFL؟",
                        "ما هو عزم شد براغي كربون التسخين CFC المقننة؟",
                        "اشرح تسلسل أمان فتح الباب ACT1 و ACT2",
                        "ما هي مواصفات قاطع مضخة الفراغ 6GQZ10؟"
                    ) else listOf(
                        "How to troubleshoot cooling water alarm 182BFL?",
                        "What are the torque specs for CFC graphite heating elements?",
                        "Explain two-hand door opening safety sequence ACT1/ACT2",
                        "What is the breaker rating for mechanical pump 6GQZ10?"
                    )

                    starterPrompts.forEach { prompt ->
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .padding(vertical = 4.dp)
                                .clickable {
                                    viewModel.updateChatInput(prompt)
                                    viewModel.sendChatMessage()
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = FurnaceAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = prompt,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(uiState.chatMessages) { message ->
                        val isThisSpeaking = speakingText == message.text
                        ChatMessageItem(
                            message = message,
                            isAr = isAr,
                            isSpeaking = isThisSpeaking,
                            onToggleSpeak = {
                                if (isThisSpeaking) {
                                    tts?.stop()
                                    speakingText = null
                                } else {
                                    tts?.stop()
                                    tts?.speak(message.text, TextToSpeech.QUEUE_FLUSH, null, "MSG_${message.timestamp}")
                                    speakingText = message.text
                                }
                            }
                        )
                    }

                    if (uiState.isAiResponding) {
                        item {
                            AiTypingIndicator(isAr = isAr)
                        }
                    }
                }
            }
        }

        // 3. Bottom Input Bar with Camera & Voice Mic
        Surface(
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Picker button for Visual Inspection
                IconButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("chat_camera_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Attach Inspection Photo",
                        tint = CyanPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Voice Mic button for hands-free audio query
                IconButton(
                    onClick = {
                        try {
                            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                putExtra(RecognizerIntent.EXTRA_PROMPT, if (isAr) "تحدث بسؤالك التقني حول الفرن..." else "Speak technical question...")
                            }
                            speechRecognizerLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Voice recognition not available on this device", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("chat_voice_mic_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = FurnaceAmber,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                OutlinedTextField(
                    value = uiState.chatInputText,
                    onValueChange = { viewModel.updateChatInput(it) },
                    placeholder = {
                        Text(
                            text = if (isAr) "اكتب أو تحدث بسؤالك التقني..." else "Type or speak question...",
                            fontSize = 13.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_textfield"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    maxLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = { viewModel.sendChatMessage() }
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { viewModel.sendChatMessage() },
                    enabled = uiState.chatInputText.isNotBlank() && !uiState.isAiResponding,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (uiState.chatInputText.isNotBlank() && !uiState.isAiResponding) CyanPrimary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .testTag("send_chat_message_btn")
                ) {
                    if (uiState.isAiResponding) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = CyanPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = if (uiState.chatInputText.isNotBlank()) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    isAr: Boolean,
    isSpeaking: Boolean = false,
    onToggleSpeak: () -> Unit = {}
) {
    val isUser = message.sender == ChatSender.USER
    val uriHandler = LocalUriHandler.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(CyanPrimary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(0.85f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = if (isUser) 14.dp else 2.dp,
                    bottomEnd = if (isUser) 2.dp else 14.dp
                ),
                color = when {
                    message.isError -> AlertRed.copy(alpha = 0.15f)
                    isUser -> CyanPrimary
                    else -> MaterialTheme.colorScheme.surfaceVariant
                },
                border = if (message.isError) androidx.compose.foundation.BorderStroke(1.dp, AlertRed) else null
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.text,
                        color = when {
                            message.isError -> AlertRed
                            isUser -> Color.Black
                            else -> MaterialTheme.colorScheme.onSurface
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (isUser) FontWeight.Medium else FontWeight.Normal,
                        lineHeight = 20.sp
                    )

                    // Audio Read Aloud button for Assistant Messages
                    if (!isUser && !message.isError) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSpeaking) CyanPrimary.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { onToggleSpeak() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                        contentDescription = if (isSpeaking) "Stop Audio" else "Read Aloud",
                                        tint = if (isSpeaking) CyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isSpeaking) (if (isAr) "إيقاف الصوت" else "Stop Audio") else (if (isAr) "استماع صوتي" else "Listen"),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isSpeaking) CyanPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Generated Image if present
                    if (message.generatedImageBase64 != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val bitmap = remember(message.generatedImageBase64) {
                            try {
                                val decodedBytes = Base64.decode(message.generatedImageBase64, Base64.DEFAULT)
                                BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Generated Technical Image",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, CyanPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            )
                        }
                    }

                    // Google Search Grounding Sources
                    if (message.groundings.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isAr) "مصادر البحث ومعايير السلامة (Search Grounding):" else "Search Grounding Verified Sources:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = CyanPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        message.groundings.forEach { source ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                                    .clickable {
                                        try {
                                            uriHandler.openUri(source.uri)
                                        } catch (e: Exception) {
                                            // Handle invalid URI
                                        }
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.OpenInBrowser,
                                        contentDescription = null,
                                        tint = CyanPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = source.title.ifBlank { source.uri },
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(FurnaceAmber.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = FurnaceAmber,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun AiTypingIndicator(isAr: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(CyanPrimary.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Psychology,
                contentDescription = null,
                tint = CyanPrimary,
                modifier = Modifier.size(14.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(14.dp),
                    color = CyanPrimary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isAr) "الذكاء الاصطناعي يقوم بالتحليل الفني..." else "Gemini is analyzing specs...",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
