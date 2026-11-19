package com.example.data

import com.example.model.SpecItem
import com.example.model.TechnicalSpecGroup

object FurnaceSpecsData {
    const val MODEL = "TPH 120/120/230"
    const val SERIAL_NUMBER = "911"
    const val YEAR = "2023"
    const val MANUFACTURER = "TAV Vacuum Furnaces S.p.A."
    const val MANUFACTURER_ADDRESS = "Via dell'Industria 11, 24043 Caravaggio (BG), Italy"
    const val CUSTOMER = "GEAT"
    const val PREFIX_PROJECT = "217022911"
    const val HIGHER_LEVEL_FUNCTION = "306000982"

    val groups = listOf(
        TechnicalSpecGroup(
            titleEn = "General & Dimensions",
            titleAr = "الأبعاد العامة وحجم التشغيل",
            items = listOf(
                SpecItem("Furnace Model", "طراز الفرن", MODEL),
                SpecItem("Serial Number", "الرقم التسلسلي", SERIAL_NUMBER),
                SpecItem("Year of Construction", "سنة الصنع", YEAR),
                SpecItem("Working Useful Width", "العرض المفيد للتشغيل", "1200", "mm"),
                SpecItem("Working Useful Height", "الارتفاع المفيد للتشغيل", "1200", "mm"),
                SpecItem("Working Useful Depth", "العمق المفيد للتشغيل", "2300", "mm"),
                SpecItem("Working Useful Volume", "الحجم المفيد للتشغيل", "3312", "dm³ (3.31 m³)"),
                SpecItem("Gross Weight of Plant", "الوزن الإجمالي للفرن", "Approx. 34", "tonnes"),
                SpecItem("Maximum Gross Charge", "أقصى حمولة تشغيلية", "2000", "kg")
            )
        ),
        TechnicalSpecGroup(
            titleEn = "Temperature Performance",
            titleAr = "أداء درجات الحرارة",
            items = listOf(
                SpecItem("Maximum Operating Temp (Baking/Cleaning)", "أقصى حرارة تشغيل (دورة الخَبز/التنظيف)", "1300", "°C"),
                SpecItem("Standard Operating Temperature", "حرارة التشغيل القياسية", "1280", "°C"),
                SpecItem("Temperature Uniformity (600°C - 1200°C)", "تجانس الحرارة في الفراغ", "± 5", "°C"),
                SpecItem("Heating Time (Room Temp to 1200°C Empty)", "زمن التسخين من حرارة الغرفة إلى 1200°م (فارغ)", "< 60", "min"),
                SpecItem("Cooling Time (1200°C to 400°C)", "زمن التبريد من 1200°م إلى 400°م", "< 20", "min"),
                SpecItem("Door Safe Opening Max Temperature", "أقصى حرارة لفتح الباب بأمان", "90 (Air Admit) / 180 Max", "°C")
            )
        ),
        TechnicalSpecGroup(
            titleEn = "Vacuum Performance",
            titleAr = "أداء نظام التفريغ الهوائي",
            items = listOf(
                SpecItem("Ultimate Vacuum Level", "أقصى مستوى تفريغ نهائي", "10⁻⁶", "torr"),
                SpecItem("Operating Vacuum Level", "مستوى تفريغ التشغيل", "10⁻⁴ / 10⁻⁵", "torr"),
                SpecItem("Partial Pressure Mode (Argon)", "الضغط الجزئي لغاز الآرجون", "0.1 – 1.0", "torr"),
                SpecItem("Leak Rate", "معدل التسريب الأقصى المسموح", "< 25", "micron/h"),
                SpecItem("Pumping Time to 1×10⁻² mbar", "زمن سحب الهواء حتى 0.01 ميلي بار", "< 15", "min")
            )
        ),
        TechnicalSpecGroup(
            titleEn = "Electrical & Energy",
            titleAr = "الكهرباء والطاقة",
            items = listOf(
                SpecItem("Heating Power", "قدرة عناصر التسخين", "480", "kW"),
                SpecItem("Connected Total Load", "إجمالي الحمل الكهربائي المتصل", "750", "kVA"),
                SpecItem("Rated Voltage & Phases", "الجهد الاسمي وعدد الأطوار", "400 V, 3-Phase, 50 Hz", ""),
                SpecItem("Distribution System", "نظام التوزيع الكهربائي", "3ph + PE", ""),
                SpecItem("Full-Load Current (Main Cabinet MiCa)", "التيار الكلي المقنن (اللوحة الرئيسية)", "1072", "A"),
                SpecItem("Full-Load Current (Furnace Cabinet FuCa)", "التيار الكلي (لوحة الفرن)", "120", "A"),
                SpecItem("Full-Load Current (Water Cabinet WaCa)", "التيار الكلي (لوحة المياه)", "57", "A"),
                SpecItem("Rated Short Time Withstand Current (Icw)", "تيار الصمود للدوائر القصيرة", "20 kA (MiCa) / 10 kA (Others)", "")
            )
        ),
        TechnicalSpecGroup(
            titleEn = "Gas & Quenching",
            titleAr = "الغازات والتبريد الفجائي",
            items = listOf(
                SpecItem("Quenching Gas", "غاز التبريد الفجائي", "Argon (Ar) & Nitrogen (N2)", ""),
                SpecItem("Quenching Gas Max Pressure", "أقصى ضغط لغاز التبريد", "6", "bar abs"),
                SpecItem("Quenching Gas Purity", "نقاوة الغاز المطلوبة", "> 99.999", "%"),
                SpecItem("Pneumatic Buffer Tank", "سعة خزان الهواء المضغوط", "50", "liters (5-6 bar)")
            )
        ),
        TechnicalSpecGroup(
            titleEn = "Water Cooling System",
            titleAr = "نظام تبريد المياه",
            items = listOf(
                SpecItem("Cooling Type", "نوع سائل التبريد", "Water (Acqua)", ""),
                SpecItem("Chamber Cooling Water Request", "تدفق مياه تبريد الغرفة", "900", "l/min"),
                SpecItem("Heat Exchanger Quenching Flow", "تدفق المبادل الحراري أثناء التبريد", "900", "l/min"),
                SpecItem("Total Flow Capacity", "إجمالي تدفق المياه", "1800", "l/min"),
                SpecItem("Water Tank Capacity", "سعة خزان المياه", "18", "m³"),
                SpecItem("Inlet Pressure Range", "ضغط المياه عند الدخول", "2.5 – 3.0", "bar"),
                SpecItem("Outlet Pressure Range", "ضغط المياه عند الخروج", "0.0 – 0.5", "bar"),
                SpecItem("Max Water Inlet Temperature", "أقصى حرارة للمياه الداخلة", "27 – 30", "°C"),
                SpecItem("Max Water Hardness", "أقصى عسورة للمياه", "15 Fr° (150 ppm CaCO₃)", ""),
                SpecItem("Emergency Water Flow", "مضخات الطوارئ بالغاز", "2x 100 l/min (Air/N2 at 4 barG)", "")
            )
        ),
        TechnicalSpecGroup(
            titleEn = "Hot Zone & Aerospace Pyrometry (AMS 2750G)",
            titleAr = "المنطقة الساخنة والمعايير الفضائية (AMS 2750G)",
            items = listOf(
                SpecItem("Hot Zone Construction", "هيكل المنطقة الساخنة", "Curved Graphite Elements + Rigid Graphite Felt (45mm)", ""),
                SpecItem("Heating Control Zones", "مناطق التحكم الحراري", "4 Independent Zones (Front, Rear, Top, Bottom)", ""),
                SpecItem("Pyrometry Class", "تصنيف التجانس الحراري", "AMS 2750G Class 2 (± 5°C / ± 10°F)", ""),
                SpecItem("Instrumentation Type", "نوع أجهزة القياس والتسجيل", "Type B (Control, Record & OverTemp)", ""),
                SpecItem("Control Thermocouples", "مجسات التحكم الرئيسية", "Type S (Pt/Pt-10%Rh) Noble Metal", ""),
                SpecItem("Load Thermocouples", "مجسات فحص الشحنة", "Type N (Nicrosil-Nisil) / Type K (max 90 uses)", ""),
                SpecItem("Hearth Load Capacity", "قدرة حمل أرضية السلات", "2000 kg gross at 1280°C (CFC rails)", ""),
                SpecItem("Thermal Expansion Clearance", "خلوص التمدد الحراري لسخانات الجرافيت", "> 50", "mm")
            )
        ),
        TechnicalSpecGroup(
            titleEn = "Control, SCADA & Safety Architecture (SIL 3)",
            titleAr = "منظومة التحكم والأتمتة والأمان الصناعي (SIL 3)",
            items = listOf(
                SpecItem("Master Controller (PLC)", "معالج الأتمتة الرئيسي", "Siemens SIMATIC S7-1513-1 PN", ""),
                SpecItem("Safety Integrated Controller", "معالج الأمان الصناعي", "Pilz Failsafe Safety Module (SIL 3 / Cat 4)", ""),
                SpecItem("Temperature Programmer", "منظم درجات الحرارة الدقيق", "Eurotherm EPC3016 PID (AMS 2750G)", ""),
                SpecItem("Independent Over-Temp Limit", "قاطع الحرارة الزائدة المستقل", "Eurotherm 3216i Hardware Cutoff", ""),
                SpecItem("SCADA Software Platform", "منظومة الإشراف والمراقبة SCADA", "WinCC Professional & Pro-face TP1200", ""),
                SpecItem("Field Communication Bus", "بروتوكول الاتصال الميداني", "PROFINET Industrial Ethernet (100 Mbps)", ""),
                SpecItem("Enclosure Protection Rating", "درجة حماية اللوحات الكهربائية", "IP54 Industrial Seal", ""),
                SpecItem("Two-Hand Door Station", "جهاز التحكم بالباب باليدين", "Pilz PIT js2 Synchronous Safety", "")
            )
        ),
        TechnicalSpecGroup(
            titleEn = "Auxiliary Equipment, Accessories & Tooling",
            titleAr = "المعدات المساعدة والملحقات والتجهيزات التكميلية",
            items = listOf(
                SpecItem("Automated Charge Loader", "عربة التحميل والتفريغ الآلية", "Motorized Heavy-Duty Rail Shuttle (2000 kg capacity)", ""),
                SpecItem("Charge Baskets & Fixtures", "سلات وأطر تثبيت الشحنات", "CFC Grid Plates & Inconel 602CA Baskets", ""),
                SpecItem("Gas Quench Buffer Vessels", "خزانات تخزين غاز التبريد المضغوط", "Vertical Pressure Vessels (16 bar, 2x 10 m³ N2/Ar)", ""),
                SpecItem("Cryogenic Gas Supply", "محطة إمداد الغاز السائل المبرد", "Liquid Nitrogen LIN Tank & Atmospheric High-Flow Vaporizer", ""),
                SpecItem("Closed-Loop Evaporative Cooler", "برج التبريد التبخيري المغلق والمبرد", "Closed Circuit Cooling Tower + 200 kW Backup Chiller", ""),
                SpecItem("Water Softening & DI Unit", "محطة معالجة وتليين المياه", "Reverse Osmosis / Water Softener Skid (<10 ppm hardness)", ""),
                SpecItem("Helium Leak Detector", "كاشف التسريب بمطياف الهيليوم", "Pfeiffer ASM 340 Mobile Leak Detector (NW40 Port)", ""),
                SpecItem("Field Dry-Block TC Calibrator", "جهاز معايرة المزدوجات الحرارية الحقلي", "Ametek / Isotech 1200°C Dry-Block Calibrator", ""),
                SpecItem("UPS Power Backup Station", "وحدة التغذية الكهربائية غير المنقطعة", "APC Smart-UPS On-Line 10 kVA (PLC, SCADA, Pirani & Penning)", ""),
                SpecItem("Air Compressor & Desiccant Dryer", "ضاغط الهواء الصناعي والمجفف الامتصاصي", "Rotary Screw Compressor (8 bar, -40°C Dew Point Class 2)", "")
            )
        )
    )
}

