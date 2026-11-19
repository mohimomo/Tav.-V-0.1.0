package com.example.ui

import com.example.model.AlarmSeverity
import com.example.model.SubsystemCategory

object AppStrings {

    fun appTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "فرن التفريغ TAV TPH 120"
        AppLanguage.FRENCH -> "Four Sous Vide TAV TPH 120"
        AppLanguage.ENGLISH -> "TAV Vacuum Furnace TPH 120"
    }

    // Top Navigation Labels
    fun navHome(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الرئيسية"
        AppLanguage.FRENCH -> "Accueil"
        AppLanguage.ENGLISH -> "Home"
    }

    fun navSearch(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "البحث الذكي"
        AppLanguage.FRENCH -> "Recherche"
        AppLanguage.ENGLISH -> "Smart Search"
    }

    fun navAlarms(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الأعطال"
        AppLanguage.FRENCH -> "Alarmes"
        AppLanguage.ENGLISH -> "Alarms"
    }

    fun navComponents(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "القطع والمخططات"
        AppLanguage.FRENCH -> "Composants & P&ID"
        AppLanguage.ENGLISH -> "Parts & P&ID"
    }

    fun navMaintenance(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الصيانة"
        AppLanguage.FRENCH -> "Maintenance"
        AppLanguage.ENGLISH -> "Maintenance"
    }

    fun navCircuits(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "المخططات والدارات"
        AppLanguage.FRENCH -> "Schémas électriques"
        AppLanguage.ENGLISH -> "Circuits & Schematics"
    }

    fun navAiChat(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "المساعد الذكي"
        AppLanguage.FRENCH -> "Assistant IA"
        AppLanguage.ENGLISH -> "AI Assistant"
    }

    fun navManual(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الدليل الميداني"
        AppLanguage.FRENCH -> "Manuel de terrain"
        AppLanguage.ENGLISH -> "Field Manual"
    }

    fun navOperation(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "دليل التشغيل"
        AppLanguage.FRENCH -> "Guide d'opération"
        AppLanguage.ENGLISH -> "Operation Guide"
    }

    fun navAbout(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "حول التطبيق"
        AppLanguage.FRENCH -> "À propos"
        AppLanguage.ENGLISH -> "About App"
    }

    fun navPresentation(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "العرض التقديمي الشامل"
        AppLanguage.FRENCH -> "Présentation de l'application"
        AppLanguage.ENGLISH -> "App Presentation Deck"
    }

    // Severity labels
    fun severityLabel(severity: AlarmSeverity, lang: AppLanguage): String = when (severity) {
        AlarmSeverity.CRITICAL -> when (lang) {
            AppLanguage.ARABIC -> "حرج / فوري"
            AppLanguage.FRENCH -> "CRITIQUE"
            AppLanguage.ENGLISH -> "CRITICAL"
        }
        AlarmSeverity.HIGH -> when (lang) {
            AppLanguage.ARABIC -> "عالي الأهمية"
            AppLanguage.FRENCH -> "ÉLEVÉ"
            AppLanguage.ENGLISH -> "HIGH"
        }
        AlarmSeverity.WARNING -> when (lang) {
            AppLanguage.ARABIC -> "تحذير"
            AppLanguage.FRENCH -> "AVERTISSEMENT"
            AppLanguage.ENGLISH -> "WARNING"
        }
    }

    // Category titles
    fun categoryTitle(category: SubsystemCategory, lang: AppLanguage): String = category.getTitle(lang)

    // Alarm detail dialog labels
    fun alarmDescription(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "وصف المشكلة / الإنذار"
        AppLanguage.FRENCH -> "Description de l'alarme / Défaut"
        AppLanguage.ENGLISH -> "Alarm Description"
    }

    fun furnaceReaction(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "استجابة ورد فعل الفرن الآلية"
        AppLanguage.FRENCH -> "Réaction automatique du four"
        AppLanguage.ENGLISH -> "Furnace Automatic Reaction"
    }

    fun causesAndRemedies(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الأسباب المحتملة والحلول الهندسية"
        AppLanguage.FRENCH -> "Causes probables et solutions techniques"
        AppLanguage.ENGLISH -> "Probable Causes & Step-by-Step Remedies"
    }

    fun schematicsAndBreakers(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "المخططات وقواطع الحماية المعنية"
        AppLanguage.FRENCH -> "Schémas et disjoncteurs associés"
        AppLanguage.ENGLISH -> "Schematics & Associated Breakers"
    }

    fun askAiAssistant(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "استشارة المساعد الذكي لهذا العطل"
        AppLanguage.FRENCH -> "Consulter l'IA pour ce défaut"
        AppLanguage.ENGLISH -> "Consult AI for this fault"
    }

    fun close(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "إغلاق"
        AppLanguage.FRENCH -> "Fermer"
        AppLanguage.ENGLISH -> "Close"
    }

    // Component details
    fun technicalSpecifications(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "المواصفات الفنية المعتمدة"
        AppLanguage.FRENCH -> "Spécifications techniques certifiées"
        AppLanguage.ENGLISH -> "Technical Specifications"
    }

    fun electricalCabinet(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "اللوحة الكهربائية"
        AppLanguage.FRENCH -> "Armoire électrique"
        AppLanguage.ENGLISH -> "Electrical Cabinet"
    }

    fun schematicsPage(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "صفحة المخطط"
        AppLanguage.FRENCH -> "Page de schéma"
        AppLanguage.ENGLISH -> "Schematic Page"
    }

    fun operationalRole(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الدور والوظيفة في الفرن"
        AppLanguage.FRENCH -> "Rôle opérationnel dans le four"
        AppLanguage.ENGLISH -> "Operational Role"
    }

    fun physicalLocation(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الموقع الفعلي على الشاسيه"
        AppLanguage.FRENCH -> "Emplacement physique sur le châssis"
        AppLanguage.ENGLISH -> "Physical Location on Skid"
    }

    // Quick Action Bar
    fun quickActions(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "الوصول السريع للأقسام"
        AppLanguage.FRENCH -> "Accès rapide aux sections"
        AppLanguage.ENGLISH -> "Quick Section Access"
    }

    fun searchPlaceholder(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "ابحث عن كود إنذار، قطعة، قاطع، أو صفحة مخطط..."
        AppLanguage.FRENCH -> "Rechercher code alarme, composant, disjoncteur..."
        AppLanguage.ENGLISH -> "Search alarm code, part, breaker, schematic..."
    }

    fun allAlarmsTab(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "جميع الإنذارات"
        AppLanguage.FRENCH -> "Toutes les alarmes"
        AppLanguage.ENGLISH -> "All Alarms"
    }

    fun bookmarkedTab(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "المفضلة"
        AppLanguage.FRENCH -> "Favoris"
        AppLanguage.ENGLISH -> "Bookmarks"
    }

    fun languageName(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> "العربية"
        AppLanguage.FRENCH -> "Français"
        AppLanguage.ENGLISH -> "English"
    }
}

fun AppLanguage.tr(ar: String, en: String, fr: String = en): String = when (this) {
    AppLanguage.ARABIC -> ar
    AppLanguage.FRENCH -> fr
    AppLanguage.ENGLISH -> en
}

val AppLanguage.isArabic: Boolean
    get() = this == AppLanguage.ARABIC

val AppLanguage.isFrench: Boolean
    get() = this == AppLanguage.FRENCH

val AppLanguage.isEnglish: Boolean
    get() = this == AppLanguage.ENGLISH

