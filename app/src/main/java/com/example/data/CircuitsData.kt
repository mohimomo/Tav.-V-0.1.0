package com.example.data

import com.example.model.CircuitConnection
import com.example.model.CircuitDiagram
import com.example.model.CircuitDomain
import com.example.model.CircuitNode
import com.example.model.FlowType
import com.example.model.NodeSymbolType
import com.example.model.TraceablePath

object CircuitsData {

    // =========================================================================
    // 1. ELECTRICAL POWER & CONTROL SCHEMATICS
    // =========================================================================
    val electricalDiagram = CircuitDiagram(
        id = "circuit_electrical",
        domain = CircuitDomain.ELECTRICAL,
        titleEn = "Electrical Power & Control Schematics",
        titleAr = "المخطط الكهربائي لتوزيع القدرة والتحكم والأتمتة",
        subtitleEn = "400V 3-Phase, 480kW Heating Loops, Vacuum Motor Drives & Safety PLC",
        subtitleAr = "تغذية 400 فولت 3 أطوار، تسخين 480 ك.و، محركات مضخات التفريغ و PLC الأمان",
        schematicsRef = "Electrical Schematics Doc. 217022911 P. 1-135",
        nodes = listOf(
            CircuitNode(
                id = "el_grid",
                labelEn = "Main Power Grid",
                labelAr = "شبكة التغذية الرئيسية",
                componentTag = "400V / 50Hz",
                symbolType = NodeSymbolType.POWER_SOURCE,
                defaultStatusEn = "Energized 400V AC",
                defaultStatusAr = "مغذي 400 فولت متناوب",
                x = 100f, y = 280f,
                rating = "400V, 3-Phase, 50Hz, 750 kVA",
                cabinet = "Factory Upstream Switchboard",
                schematicsPage = "P. 5",
                descriptionEn = "Main incoming 3-phase electrical power line delivering up to 750 kVA.",
                descriptionAr = "خط التغذية الكهربائي الرئيسي ثلاثي الأطوار المغذي للمصنع بحمل 750 ك.ف.أ.",
                isPrimarySource = true
            ),
            CircuitNode(
                id = "el_isolator",
                labelEn = "Main Isolator Switch",
                labelAr = "القاطع العازل الرئيسي",
                componentTag = "Q0",
                symbolType = NodeSymbolType.BREAKER,
                defaultStatusEn = "Closed / ON",
                defaultStatusAr = "مغلق / قيد التوصيل",
                x = 240f, y = 280f,
                rating = "1250 A / 690 V",
                cabinet = "Main Cabinet (MiCa)",
                schematicsPage = "P. 8",
                descriptionEn = "Heavy duty lockable main disconnect switch. WARNING: Orange wires stay live even when OFF!",
                descriptionAr = "قاطع العزل الرئيسي القابل للقفل. تحذير: الأسلاك البرتقالية تبقى مكهربة حتى بعد فصله!",
                relatedAlarms = listOf("Side cabinet disconnected alarm")
            ),
            // Heating Branch
            CircuitNode(
                id = "el_contactor_heat",
                labelEn = "Heating Safety Contactor",
                labelAr = "كونتكتور أمان التسخين",
                componentTag = "152QAC",
                symbolType = NodeSymbolType.CONTACTOR,
                defaultStatusEn = "Enabled in Auto Cycle",
                defaultStatusAr = "مفعل أثناء الدورة الآلية",
                x = 420f, y = 120f,
                rating = "3-pole, 800A, 24V DC coil",
                cabinet = "Main Cabinet (MiCa)",
                schematicsPage = "P. 18",
                descriptionEn = "Main heating line contactor interlocking heating enable with vacuum and water safety interlocks.",
                descriptionAr = "كونتكتور خط التسخين الرئيسي المشروط بأمان التفريغ وتدفق مياه التبريد.",
                relatedAlarms = listOf("152QAC contactor feedback fault")
            ),
            CircuitNode(
                id = "el_thyristor",
                labelEn = "Thyristor Power Controller",
                labelAr = "وحدة الثايرستور للتحكم بالقدرة",
                componentTag = "TC1",
                symbolType = NodeSymbolType.THYRISTOR,
                defaultStatusEn = "PID Phase Angle Modulating",
                defaultStatusAr = "تعديل زاوية الطور بنظام PID",
                x = 600f, y = 120f,
                rating = "Eurotherm TE10A / 400V 600A",
                cabinet = "Furnace Cabinet (FuCa)",
                schematicsPage = "P. 22",
                descriptionEn = "Solid-state thyristor power regulator adjusting electrical power delivered to heating elements based on PID setpoint.",
                descriptionAr = "منظم قدرة الثايرستور الإلكتروني للتحكم الدقيق في الطاقة المسلمة لعناصر التسخين بنظام PID.",
                relatedAlarms = listOf("SCR overtemperature alarm")
            ),
            CircuitNode(
                id = "el_transformer",
                labelEn = "Heating Power Transformer",
                labelAr = "محول قدرة التسخين",
                componentTag = "Tr1",
                symbolType = NodeSymbolType.TRANSFORMER,
                defaultStatusEn = "Step-Down 400V to Low Voltage",
                defaultStatusAr = "خفض الجهد من 400ف إلى جهد منخفض وتيار عالٍ",
                x = 760f, y = 120f,
                rating = "480 kVA, Primary 400V, Secondary 65V",
                cabinet = "Transformer Bay (TrBay)",
                schematicsPage = "P. 26",
                descriptionEn = "Heavy duty water-cooled transformer supplying high amperage at low voltage to graphite elements.",
                descriptionAr = "محول مبرد بالمياه يحول الجهد العالي إلى تيار ضخم بجهد منخفض لتغذية عناصر الكربون CFC."
            ),
            CircuitNode(
                id = "el_graphite_resistors",
                labelEn = "CFC Graphite Heating Elements",
                labelAr = "عناصر التسخين الكربونية CFC",
                componentTag = "HeCh",
                symbolType = NodeSymbolType.HEATING_ELEMENT,
                defaultStatusEn = "Max 1300 °C Uniform",
                defaultStatusAr = "تسخين حتى 1300°م بتجانس ±5°م",
                x = 920f, y = 120f,
                rating = "480 kW total thermal rating",
                cabinet = "Vacuum Vessel Interior",
                schematicsPage = "P. 30",
                descriptionEn = "Carbon-fiber composite (CFC) graphite heating elements bolted with precision torques (M14=2.5Nm, M16=3.5Nm, M24=8Nm).",
                descriptionAr = "عناصر تسخين كربونية CFC مربوطة بعزوم مقننة دقيقة داخل الغرفة المعزولة بموليبدينوم وكربون.",
                isEndpoint = true
            ),
            // Motor / Pump Branch
            CircuitNode(
                id = "el_breaker_mepu1",
                labelEn = "MePu1 Motor Circuit Breaker",
                labelAr = "قاطع محرك المضخة الميكانيكية 1",
                componentTag = "6GQZ10",
                symbolType = NodeSymbolType.BREAKER,
                defaultStatusEn = "Thermal-Magnetic OK",
                defaultStatusAr = "القاطع الحراري المغناطيسي سليم",
                x = 420f, y = 280f,
                rating = "32A / Ir=24-32A trip",
                cabinet = "Main Cabinet (MiCa)",
                schematicsPage = "P. 35",
                descriptionEn = "Motor protective breaker with auxiliary contact monitoring trip status.",
                descriptionAr = "قاطع حماية محرك مضخة التفريغ مع نقاط تلامس مساعدة لمراقبة الفصل والإنذار.",
                relatedAlarms = listOf("6GQZ10 mechanical pump breaker tripped")
            ),
            CircuitNode(
                id = "el_contactor_mepu1",
                labelEn = "MePu1 Motor Contactor",
                labelAr = "كونتكتور تشغيل MePu1",
                componentTag = "6MMQ11",
                symbolType = NodeSymbolType.CONTACTOR,
                defaultStatusEn = "Running / Engaged",
                defaultStatusAr = "قيد التشغيل والتوصيل",
                x = 600f, y = 280f,
                rating = "18.5 kW AC-3, 24V DC coil",
                cabinet = "Main Cabinet (MiCa)",
                schematicsPage = "P. 36",
                descriptionEn = "Main line contactor powering the primary Leybold SV630B mechanical vacuum pump.",
                descriptionAr = "كونتكتور توصيل محرك مضخة التفريغ الميكانيكية الابتدائية Leybold SV630B."
            ),
            CircuitNode(
                id = "el_mepu1_motor",
                labelEn = "Leybold SV630B Pump Motor",
                labelAr = "محرك مضخة Leybold SV630B",
                componentTag = "MePu1",
                symbolType = NodeSymbolType.MOTOR_PUMP,
                defaultStatusEn = "Running 700 m³/h",
                defaultStatusAr = "تعمل بتدفق 700 م³/ساعة",
                x = 760f, y = 280f,
                rating = "15 kW, 1450 rpm, 400V 3Ph",
                cabinet = "Vacuum Pumping Skid",
                schematicsPage = "P. 40",
                descriptionEn = "Primary roughing vacuum pump evacuating chamber from atmosphere to base roughing level.",
                descriptionAr = "المضخة الميكانيكية الرئيسية لسحب الهواء من الضغط الجوي حتى الفراغ الأولي.",
                isEndpoint = true
            ),
            // Automation & Safety PLC Branch
            CircuitNode(
                id = "el_psu_24v",
                labelEn = "Regulated 24V DC Power Supply",
                labelAr = "وحدة تغذية 24 فولت المستمر",
                componentTag = "G1",
                symbolType = NodeSymbolType.POWER_SOURCE,
                defaultStatusEn = "24.0 V DC Stable",
                defaultStatusAr = "24.0 فولت مستمر مستقر",
                x = 420f, y = 450f,
                rating = "400V AC in -> 24V DC / 40A out",
                cabinet = "Main Cabinet (MiCa)",
                schematicsPage = "P. 45",
                descriptionEn = "Industrial switched-mode power supply backing automation, sensors, and safety interlocks.",
                descriptionAr = "وحدة تغذية نبضية صناعية لتشغيل أجهزة التحكم، الحساسات وصمامات الهواء."
            ),
            CircuitNode(
                id = "el_plc_siemens",
                labelEn = "Siemens S7-1513 Main PLC",
                labelAr = "كمبيوتر التحكم سيمنز S7-1513",
                componentTag = "PLC-A1",
                symbolType = NodeSymbolType.PLC_MODULE,
                defaultStatusEn = "RUN Mode / Cyclic 10ms",
                defaultStatusAr = "وضع التشغيل RUN / دورة 10 مللي ثانية",
                x = 600f, y = 450f,
                rating = "CPU 1513-1 PN + Profinet / AS-i",
                cabinet = "Main Cabinet (MiCa)",
                schematicsPage = "P. 50",
                descriptionEn = "Primary industrial process controller executing recipes, vacuum sequences, and temperature PID.",
                descriptionAr = "المعالج الصناعي الرئيسي المسؤول عن تنفيذ الوصفات وتسلسل التفريغ وضبط درجات الحرارة."
            ),
            CircuitNode(
                id = "el_pilz_safety",
                labelEn = "Pilz PSS 4000 Safety PLC",
                labelAr = "كمبيوتر الأمان بيلز PSS 4000",
                componentTag = "PILZ-S1",
                symbolType = NodeSymbolType.PLC_MODULE,
                defaultStatusEn = "Failsafe Dual-Channel OK",
                defaultStatusAr = "قناة أمان مزدوجة متطابقة سليمة",
                x = 760f, y = 450f,
                rating = "EN 13849-1 SIL 3 / PL e",
                cabinet = "Main Cabinet (MiCa)",
                schematicsPage = "P. 55",
                descriptionEn = "Redundant failsafe safety controller supervising Emergency Stops, two-hand door controls, and vacuum/thermal safety interlocks.",
                descriptionAr = "كمبيوتر الأمان الحرج ثنائي المعالجات لمراقبة أزرار الطوارئ وتحكم الباب باليدين وأقفال السلامة."
            ),
            CircuitNode(
                id = "el_estop_loop",
                labelEn = "Emergency Stop Safety Loop",
                labelAr = "حلقة أزرار التوقف في حالات الطوارئ",
                componentTag = "E-STOP",
                symbolType = NodeSymbolType.SAFETY_STATION,
                defaultStatusEn = "Healthy Closed Loop",
                defaultStatusAr = "حلقة مغلقة وسليمة",
                x = 920f, y = 450f,
                rating = "Dual-channel 24V failsafe",
                cabinet = "Door, MiCa & Remote Station",
                schematicsPage = "P. 60",
                descriptionEn = "Hardwired Emergency Stop pushbuttons positioned on Main Cabinet, door frame, and remote pendant.",
                descriptionAr = "أزرار الإيقاف الطارئ المنتشرة على اللوحة الرئيسية وجدار الفرن ومحطة التحكم المحمولة.",
                isEndpoint = true
            )
        ),
        connections = listOf(
            CircuitConnection("el_grid", "el_isolator", FlowType.ELECTRIC_3PHASE, "400V Main Line", "خط 400 فولت الرئيسي"),
            // Heating line
            CircuitConnection("el_isolator", "el_contactor_heat", FlowType.ELECTRIC_3PHASE, "Power Feeder", "مغذي القدرة"),
            CircuitConnection("el_contactor_heat", "el_thyristor", FlowType.ELECTRIC_3PHASE, "Switched 400V", "400 فولت موصول"),
            CircuitConnection("el_thyristor", "el_transformer", FlowType.ELECTRIC_3PHASE, "Phase-Angle Power", "تيار محكوم بالزاوية"),
            CircuitConnection("el_transformer", "el_graphite_resistors", FlowType.ELECTRIC_3PHASE, "Heavy Current 65V", "تيار ضخم بجهد 65ف"),
            // Pump line
            CircuitConnection("el_isolator", "el_breaker_mepu1", FlowType.ELECTRIC_3PHASE, "Motor Sub-Distribution", "توزيع المحركات"),
            CircuitConnection("el_breaker_mepu1", "el_contactor_mepu1", FlowType.ELECTRIC_3PHASE, "Protected Line", "خط محمي"),
            CircuitConnection("el_contactor_mepu1", "el_mepu1_motor", FlowType.ELECTRIC_3PHASE, "3-Phase 15kW Feed", "تغذية 3 أطوار 15 ك.و"),
            // Control line
            CircuitConnection("el_isolator", "el_psu_24v", FlowType.ELECTRIC_CONTROL, "Control Power Feed", "تغذية دوائر التحكم"),
            CircuitConnection("el_psu_24v", "el_plc_siemens", FlowType.ELECTRIC_CONTROL, "24V DC Supply", "تغذية 24 فولت مستمر"),
            CircuitConnection("el_psu_24v", "el_pilz_safety", FlowType.ELECTRIC_CONTROL, "Failsafe 24V Feed", "تغذية أمان 24 فولت"),
            CircuitConnection("el_pilz_safety", "el_estop_loop", FlowType.SAFETY_SIGNAL, "Safety Loop Monitor", "مراقبة حلقة الأمان")
        ),
        traceablePaths = listOf(
            TraceablePath(
                id = "path_heating_power",
                nameEn = "480 kW Heating Power Circuit",
                nameAr = "مسار قدرة التسخين 480 كيلوواط",
                descriptionEn = "Traces power flow from 400V grid through isolator, safety contactor 152QAC, thyristor TC1, transformer Tr1 to CFC graphite elements.",
                descriptionAr = "تتبع مسار الطاقة الكهربائية من شبكة 400ف عبر القاطع وكونتكتور 152QAC والثايرستور والمحول حتى عناصر الكربون.",
                nodeIds = listOf("el_grid", "el_isolator", "el_contactor_heat", "el_thyristor", "el_transformer", "el_graphite_resistors"),
                flowColorHex = 0xFFFF5722
            ),
            TraceablePath(
                id = "path_vacuum_motor",
                nameEn = "Mechanical Vacuum Pump Circuit",
                nameAr = "مسار تشغيل مضخة التفريغ الميكانيكية",
                descriptionEn = "Traces electrical drive circuit from breaker 6GQZ10 through contactor 6MMQ11 to Leybold SV630B pump motor.",
                descriptionAr = "تتبع دارة تشغيل محرك مضخة التفريغ من القاطع 6GQZ10 والكونتكتور 6MMQ11 حتى المحرك 15 ك.و.",
                nodeIds = listOf("el_grid", "el_isolator", "el_breaker_mepu1", "el_contactor_mepu1", "el_mepu1_motor"),
                flowColorHex = 0xFF00E5FF
            ),
            TraceablePath(
                id = "path_safety_estop",
                nameEn = "Failsafe Safety PLC & E-Stop Loop",
                nameAr = "مسار PLC الأمان وحلقة التوقف الطارئ",
                descriptionEn = "Traces 24V DC failsafe control supply through Pilz PSS 4000 dual-processor to Emergency Stop loop.",
                descriptionAr = "تتبع دارة تغذية الأمان 24 فولت عبر كمبيوتر بيلز PSS 4000 وحلقة أزرار الطوارئ.",
                nodeIds = listOf("el_grid", "el_isolator", "el_psu_24v", "el_pilz_safety", "el_estop_loop"),
                flowColorHex = 0xFF4CAF50
            )
        )
    )

    // =========================================================================
    // 2. WATER COOLING & EMERGENCY GAS PUMP SCHEMATICS
    // =========================================================================
    val waterDiagram = CircuitDiagram(
        id = "circuit_water",
        domain = CircuitDomain.WATER_COOLING,
        titleEn = "Water Cooling & Emergency System",
        titleAr = "المخطط الهيدروليكي لمياه التبريد ومضخة الطوارئ بالغاز",
        subtitleEn = "Inlet Manifold, Double-Jacket Vessel, Diffusion Pump Coil & Gas-Driven Backup",
        subtitleAr = "موزع المياه، تبريد جدار الفرن المزدوج، ملف مضخة الانتشار، ومضخة الطوارئ",
        schematicsRef = "P&ID Schematics Doc. 911_man_eng_a01 P. 6-12",
        nodes = listOf(
            CircuitNode(
                id = "wat_inlet",
                labelEn = "Cooling Water Inlet",
                labelAr = "مدخل مياه التبريد الرئيسي",
                componentTag = "WaIn",
                symbolType = NodeSymbolType.POWER_SOURCE,
                defaultStatusEn = "2.5 - 3.0 bar, <= 27°C",
                defaultStatusAr = "ضغط 2.5 - 3.0 بار، حرارة <= 27°م",
                x = 100f, y = 280f,
                rating = "Pressure 2.5-3.0 bar, Hardness <= 15 Fr°",
                cabinet = "Water Manifold Skid",
                schematicsPage = "P. 6",
                descriptionEn = "Primary treated industrial cooling water supply with inlet filter and pressure gauge.",
                descriptionAr = "خط إمداد مياه التبريد المعالجة (عسورة أقل من 15 درجة فرنسية) مع فلتر ومقياس ضغط.",
                isPrimarySource = true
            ),
            CircuitNode(
                id = "wat_pressure_switch",
                labelEn = "Inlet Pressure Sensor",
                labelAr = "حساس ضغط مياه الدخول",
                componentTag = "181BPS",
                symbolType = NodeSymbolType.SENSOR,
                defaultStatusEn = "OK (> 2.2 bar)",
                defaultStatusAr = "الضغط سليم (أعلى من 2.2 بار)",
                x = 240f, y = 280f,
                rating = "IFM PN2094 / 0-10 bar analog",
                cabinet = "Water Cabinet (WaCa)",
                schematicsPage = "P. 8",
                descriptionEn = "Monitors cooling water inlet pressure. Triggers alarm and heating cutoff if water pressure drops below safe threshold.",
                descriptionAr = "يراقب ضغط مياه التبريد ويقطع التسخين فوراً في حال انخفاض الضغط عن الحد الآمن.",
                relatedAlarms = listOf("Cooling water pressure low alarm")
            ),
            CircuitNode(
                id = "wat_manifold",
                labelEn = "Water Distribution Manifold",
                labelAr = "موزع تدفق المياه الرئيسي (Manifold)",
                componentTag = "MaWa",
                symbolType = NodeSymbolType.TANK,
                defaultStatusEn = "Distributing 900 l/min",
                defaultStatusAr = "توزيع تدفق 900 لتر/دقيقة",
                x = 400f, y = 280f,
                rating = "Stainless Steel 316, 4 distribution lines",
                cabinet = "Water Distribution Station",
                schematicsPage = "P. 10",
                descriptionEn = "Distributes cooling water to furnace vessel jacket, door flange, diffusion pump body, and heat exchanger.",
                descriptionAr = "يوزع مياه التبريد على جدار الفرن المزدوج، شفة الباب، غلاف مضخة الانتشار، والمبادل الحراري."
            ),
            // Branch A: Vessel Jacket
            CircuitNode(
                id = "wat_vessel_jacket",
                labelEn = "Vessel Double-Jacket Cooling",
                labelAr = "غلاف جدار الفرن المزدوج",
                componentTag = "VeWa",
                symbolType = NodeSymbolType.VESSEL,
                defaultStatusEn = "Flowing 400 l/min",
                defaultStatusAr = "تدفق مستمر 400 لتر/دقيقة",
                x = 620f, y = 120f,
                rating = "Double wall 304L, Max temp < 55°C",
                cabinet = "Furnace Body",
                schematicsPage = "P. 12",
                descriptionEn = "Double jacket around entire vacuum vessel maintaining external skin temperature below 50°C during 1300°C runs.",
                descriptionAr = "جدار مزدوج يحيط بالفرن بالكامل لحماية الهيكل المعدني والحفاظ على برودته أثناء درجات حرارة 1300°م."
            ),
            CircuitNode(
                id = "wat_flow_switch_vessel",
                labelEn = "Vessel Water Flow Switch",
                labelAr = "حساس تدفق مياه الغلاف",
                componentTag = "182BFL",
                symbolType = NodeSymbolType.SENSOR,
                defaultStatusEn = "Flow Switch Closed (Normal)",
                defaultStatusAr = "تدفق طبيعي وسليم",
                x = 780f, y = 120f,
                rating = "IFM SI5000 / Paddle type",
                cabinet = "Water Cabinet (WaCa)",
                schematicsPage = "P. 14",
                descriptionEn = "Monitors flow rate in vessel jacket. Immediate trip on heating if flow ceases.",
                descriptionAr = "يراقب استمرار تدفق المياه في جدار الفرن ويوقف التسخين فور انقطاعه.",
                relatedAlarms = listOf("Vessel water flow switch alarm")
            ),
            // Branch B: Diffusion Pump Cooling
            CircuitNode(
                id = "wat_diff_pump_coil",
                labelEn = "Diffusion Pump Cooling Coil",
                labelAr = "ملف تبريد مضخة الانتشار NHS 35",
                componentTag = "DiPuWa",
                symbolType = NodeSymbolType.MOTOR_PUMP,
                defaultStatusEn = "Cooling Vapor Cones",
                defaultStatusAr = "تكثيف أبخرة زيت السيليكون",
                x = 620f, y = 280f,
                rating = "Flow 50 l/min, inlet temp < 25°C",
                cabinet = "Diffusion Pump Frame",
                schematicsPage = "P. 16",
                descriptionEn = "Crucial water cooling coil condensing boiling silicone oil vapor back to boiler. Loss of cooling causes pump failure.",
                descriptionAr = "ملف تبريد فائق الأهمية لتكثيف أبخرة زيت السيليكون الساخنة (230°م). انقطاعه يسبب تلفاً للمضخة."
            ),
            // Branch C: Quenching Heat Exchanger
            CircuitNode(
                id = "wat_heat_exchanger",
                labelEn = "Gas Quench Heat Exchanger",
                labelAr = "المبادل الحراري للتبريد الفجائي بالغاز",
                componentTag = "HeEx",
                symbolType = NodeSymbolType.HEAT_EXCHANGER,
                defaultStatusEn = "Ready for Quench",
                defaultStatusAr = "جاهز للتبريد الفجائي",
                x = 620f, y = 440f,
                rating = "Copper-finned, 900 l/min during quenching",
                cabinet = "Internal Quench Plenum",
                schematicsPage = "P. 18",
                descriptionEn = "Heavy duty water-to-gas heat exchanger cooling recirculated pressurized argon from 1200°C down to 400°C in <20 min.",
                descriptionAr = "مبادل حراري ضخم لتبريد غاز الآرجون المضغوط (6 بار) المدور بالمروحة لخفض الحرارة بسرعة فائقة."
            ),
            // Return & Drain
            CircuitNode(
                id = "wat_return",
                labelEn = "Return Water Manifold",
                labelAr = "موزع المياه الراجعة إلى الشيلر",
                componentTag = "WaOut",
                symbolType = NodeSymbolType.TANK,
                defaultStatusEn = "Pressure 0 - 0.5 bar",
                defaultStatusAr = "ضغط 0 - 0.5 بار راجع للشيلر",
                x = 920f, y = 280f,
                rating = "Gravity / Low backpressure return",
                cabinet = "Return Pipe to Chiller",
                schematicsPage = "P. 20",
                descriptionEn = "Discharges heated cooling water back to cooling tower or industrial chiller for re-cooling.",
                descriptionAr = "تصريف المياه الساخنة الراجعة إلى أبراج التبريد أو الشيلر الصناعي لإعادة تبريدها.",
                isEndpoint = true
            ),
            // Emergency Circuit
            CircuitNode(
                id = "wat_emergency_pump",
                labelEn = "Emergency Gas-Driven Water Pump",
                labelAr = "مضخة مياه الطوارئ بالغاز المضغوط",
                componentTag = "EmPu1",
                symbolType = NodeSymbolType.MOTOR_PUMP,
                defaultStatusEn = "Automatic Standby",
                defaultStatusAr = "جاهزية آلية للطوارئ",
                x = 400f, y = 120f,
                rating = "Gas consumption 30 Nm³/h @ 5 barG",
                cabinet = "Emergency Water Station",
                schematicsPage = "P. 22",
                descriptionEn = "Pneumatically operated emergency pump powered by pressurized inert gas (Argon/Nitrogen) that automatically circulates water during power blackout!",
                descriptionAr = "مضخة طوارئ تعمل بضغط الغاز الخامل وتضخ المياه تلقائياً عند انقطاع الكهرباء لحماية الفرن من الذوبان والتلف.",
                relatedAlarms = listOf("Emergency gas water pump test fail")
            )
        ),
        connections = listOf(
            CircuitConnection("wat_inlet", "wat_pressure_switch", FlowType.WATER_SUPPLY, "Inlet Line", "خط الدخول"),
            CircuitConnection("wat_pressure_switch", "wat_manifold", FlowType.WATER_SUPPLY, "Supply Manifold", "موزع التغذية"),
            // Normal branches
            CircuitConnection("wat_manifold", "wat_vessel_jacket", FlowType.WATER_SUPPLY, "Vessel Supply", "تغذية الجدار"),
            CircuitConnection("wat_vessel_jacket", "wat_flow_switch_vessel", FlowType.WATER_SUPPLY, "Outlet Flow", "تدفق الخروج"),
            CircuitConnection("wat_flow_switch_vessel", "wat_return", FlowType.WATER_RETURN, "Vessel Return", "راجع الجدار"),
            CircuitConnection("wat_manifold", "wat_diff_pump_coil", FlowType.WATER_SUPPLY, "Diffusion Coil", "ملف الانتشار"),
            CircuitConnection("wat_diff_pump_coil", "wat_return", FlowType.WATER_RETURN, "Diffusion Return", "راجع الانتشار"),
            CircuitConnection("wat_manifold", "wat_heat_exchanger", FlowType.WATER_SUPPLY, "Exchanger Supply", "تغذية المبادل"),
            CircuitConnection("wat_heat_exchanger", "wat_return", FlowType.WATER_RETURN, "Exchanger Return", "راجع المبادل"),
            // Emergency loop
            CircuitConnection("wat_emergency_pump", "wat_vessel_jacket", FlowType.WATER_EMERGENCY, "Gas-Driven Water", "مياه مدفوعة بالغاز")
        ),
        traceablePaths = listOf(
            TraceablePath(
                id = "path_vessel_cooling",
                nameEn = "Vessel Primary Cooling Circuit",
                nameAr = "دارة تبريد جدار الفرن الرئيسية",
                descriptionEn = "Traces water flow through inlet, distribution manifold, double-jacket walls, and flow switch to chiller return.",
                descriptionAr = "تتبع مسار المياه من المدخل عبر الموزع وجدار الفرن المزدوج وحساس التدفق حتى الراجع.",
                nodeIds = listOf("wat_inlet", "wat_pressure_switch", "wat_manifold", "wat_vessel_jacket", "wat_flow_switch_vessel", "wat_return"),
                flowColorHex = 0xFF00E5FF
            ),
            TraceablePath(
                id = "path_emergency_water",
                nameEn = "Emergency Gas-Driven Water Backup",
                nameAr = "دارة مياه الطوارئ بمضخة الغاز المستقلة",
                descriptionEn = "Traces failsafe emergency water backup circuit activated by inert gas when blackout or primary pressure loss occurs.",
                descriptionAr = "تتبع دارة مياه الطوارئ التي تعمل بالغاز الخامل لحماية الفرن عند انقطاع التيار الكهربائي.",
                nodeIds = listOf("wat_emergency_pump", "wat_vessel_jacket", "wat_flow_switch_vessel", "wat_return"),
                flowColorHex = 0xFFFF9800
            ),
            TraceablePath(
                id = "path_diffusion_cooling",
                nameEn = "Diffusion Pump Cooling Circuit",
                nameAr = "دارة تبريد مضخة الانتشار NHS 35",
                descriptionEn = "Traces dedicated cooling loop condensing high-temperature silicone oil in Varian NHS 35 diffusion pump.",
                descriptionAr = "تتبع دارة تبريد مضخة الفراغ العالي لتكثيف أبخرة الزيت ومنع انفجار المضخة.",
                nodeIds = listOf("wat_inlet", "wat_pressure_switch", "wat_manifold", "wat_diff_pump_coil", "wat_return"),
                flowColorHex = 0xFF2196F3
            )
        )
    )

    // =========================================================================
    // 3. GAS QUENCHING & PNEUMATIC CONTROL SCHEMATICS
    // =========================================================================
    val gasPneumaticDiagram = CircuitDiagram(
        id = "circuit_gas_pneumatic",
        domain = CircuitDomain.GAS_PNEUMATIC,
        titleEn = "Gas Quenching & Pneumatic Schematics",
        titleAr = "المخطط النيوماتيكي والغازي (التبريد الفجائي والأتمتة)",
        subtitleEn = "6 bar Argon Quenching, Buffer Tank 50L & High Vacuum Gate Actuators",
        subtitleAr = "تبريد فجائي بالآرجون 6 بار، خزان نيوماتيك 50 لتر، ومشغلات صمامات الفراغ",
        schematicsRef = "P&ID Schematics Doc. 911_man_eng_a01 P. 8-15",
        nodes = listOf(
            CircuitNode(
                id = "gas_argon_source",
                labelEn = "Argon Quench Gas Supply",
                labelAr = "خزان غاز الآرجون للتبريد",
                componentTag = "Ar-Tank",
                symbolType = NodeSymbolType.TANK,
                defaultStatusEn = "Purity > 99.999%",
                defaultStatusAr = "نقاوة أعلى من 99.999%",
                x = 100f, y = 140f,
                rating = "Pressure 10-16 bar source, Ar grade 5.0",
                cabinet = "Gas Supply Yard",
                schematicsPage = "P. 8",
                descriptionEn = "High purity inert Argon storage tank feeding rapid furnace cooling and partial pressure systems.",
                descriptionAr = "خزان غاز الآرجون عالي النقاوة لتغذية التبريد الفجائي والضغط الجزئي داخل الفرن.",
                isPrimarySource = true
            ),
            CircuitNode(
                id = "gas_regulator",
                labelEn = "Quench Gas Pressure Regulator",
                labelAr = "منظم ضغط غاز التبريد",
                componentTag = "PR-Ar",
                symbolType = NodeSymbolType.VALVE,
                defaultStatusEn = "Regulating to 6 bar abs.",
                defaultStatusAr = "تنظيم الضغط إلى 6 بار مطلق",
                x = 260f, y = 140f,
                rating = "Inlet 16 bar -> Outlet 6 bar abs.",
                cabinet = "Gas Skid (FuCa Side)",
                schematicsPage = "P. 9",
                descriptionEn = "Dual-stage pressure reducing regulator ensuring furnace never exceeds CE certified 6 bar absolute vessel pressure.",
                descriptionAr = "منظم ضغط يضمن عدم تجاوز ضغط الفرن حد الأمان المصادق عليه (6 بار مطلق).",
                relatedAlarms = listOf("Chamber overpressure alarm")
            ),
            CircuitNode(
                id = "gas_fast_inflow_valve",
                labelEn = "Fast Quench Inflow Valve",
                labelAr = "صمام دخول الغاز السريع",
                componentTag = "VaGas",
                symbolType = NodeSymbolType.VALVE,
                defaultStatusEn = "Pneumatic Fast-Acting",
                defaultStatusAr = "صمام كروي سريع بمكبس نيوماتيكي",
                x = 440f, y = 140f,
                rating = "DN100, Actuated in < 2 seconds",
                cabinet = "Gas Feed Manifold",
                schematicsPage = "P. 10",
                descriptionEn = "Large bore fast actuation valve flooding chamber with 6 bar Argon at start of gas quench cycle.",
                descriptionAr = "صمام ذو قطر واسع يفتح في أقل من ثانيتين لملء الغرفة بالغاز الخامل وبدء التبريد السريع."
            ),
            CircuitNode(
                id = "gas_partial_valve",
                labelEn = "Partial Pressure Micro-Valve",
                labelAr = "صمام الضغط الجزئي الميكرومتري",
                componentTag = "PaPrVa",
                symbolType = NodeSymbolType.VALVE,
                defaultStatusEn = "Micrometric Modulating",
                defaultStatusAr = "تعديل تدفق دقيق (0.1 - 1.0 تور)",
                x = 440f, y = 40f,
                rating = "0.1 to 1.0 torr regulation",
                cabinet = "Gas Skid",
                schematicsPage = "P. 11",
                descriptionEn = "Micro-dosing inert gas valve maintaining positive partial pressure to suppress chromium and alloy sublimation.",
                descriptionAr = "صمام حقن ميكرومتري يحافظ على ضغط جزئي خفيف يمنع تطاير وتبخر سبائك الكروم."
            ),
            CircuitNode(
                id = "gas_vessel_chamber",
                labelEn = "Furnace Vacuum Chamber",
                labelAr = "غرفة التفريغ والمعالجة الحرارية",
                componentTag = "Chamber",
                symbolType = NodeSymbolType.VESSEL,
                defaultStatusEn = "Atmosphere to 10⁻⁶ torr / 6 bar Ar",
                defaultStatusAr = "من فراغ 10⁻⁶ تور حتى 6 بار آرجون",
                x = 640f, y = 140f,
                rating = "Useful volume 3312 dm³, 2000 kg gross",
                cabinet = "Furnace Body",
                schematicsPage = "P. 12",
                descriptionEn = "Double jacket water-cooled vacuum vessel housing heating chamber and workpiece loads.",
                descriptionAr = "غرفة الفرن الفولاذية المزدوجة المعزولة بالماء الحاضنة للشحنة وعناصر التسخين.",
                isEndpoint = true
            ),
            // Pneumatics Section
            CircuitNode(
                id = "pneu_air_supply",
                labelEn = "Compressed Air Supply",
                labelAr = "تغذية الهواء المضغوط للمصنع",
                componentTag = "Air-In",
                symbolType = NodeSymbolType.POWER_SOURCE,
                defaultStatusEn = "6 - 8 bar Supply",
                defaultStatusAr = "ضغط 6 - 8 بار جاف",
                x = 100f, y = 440f,
                rating = "Filter Class 4 / Dew point -20°C",
                cabinet = "Factory Pneumatic Line",
                schematicsPage = "P. 13",
                descriptionEn = "Clean, dry compressed air supply filtered and regulated for pneumatic instrumentation.",
                descriptionAr = "خط الهواء المضغوط المفلتر والجاف لتشغيل الأجهزة والصمامات النيوماتيكية.",
                isPrimarySource = true
            ),
            CircuitNode(
                id = "pneu_buffer_tank",
                labelEn = "Pneumatic 50L Buffer Tank",
                labelAr = "خزان الهواء المضغوط سعة 50 لتراً",
                componentTag = "PnTank",
                symbolType = NodeSymbolType.TANK,
                defaultStatusEn = "Charged 5 - 6 bar",
                defaultStatusAr = "مشحون بضغط 5 - 6 بار",
                x = 260f, y = 440f,
                rating = "50 Liters CE Pressure Vessel",
                cabinet = "Pneumatic Service Base",
                schematicsPage = "P. 14",
                descriptionEn = "Dedicated buffer reservoir storing sufficient pneumatic volume to guarantee safe valve shutdown during air supply drops.",
                descriptionAr = "خزان سعة 50 لتراً يضمن طاقة كافية لغلق وفتح الصمامات بأمان في حال هبوط ضغط الهواء الخارجي."
            ),
            CircuitNode(
                id = "pneu_vent_valve",
                labelEn = "Manual Tank Vent Valve",
                labelAr = "محبس تصريف الخزان اليدوي (LOTO)",
                componentTag = "PnViMaVa",
                symbolType = NodeSymbolType.VALVE,
                defaultStatusEn = "Closed in Run (Open for LOTO)",
                defaultStatusAr = "مغلق أثناء التشغيل (يفتح للصيانة)",
                x = 260f, y = 540f,
                rating = "1/2\" lockable ball valve",
                cabinet = "Pneumatic Service Base",
                schematicsPage = "P. 14",
                descriptionEn = "Lockable venting valve required by LOTO maintenance rules to dump all stored pneumatic energy before servicing.",
                descriptionAr = "صمام يدوي قابل للقفل لتفريغ كامل طاقة الهواء المضغوط قبل إجراء أي صيانة وفق معايير LOTO."
            ),
            CircuitNode(
                id = "pneu_solenoid_manifold",
                labelEn = "Pneumatic Solenoid Block",
                labelAr = "مجموعة الصمامات الكهرونيوماتيكية (Solenoids)",
                componentTag = "EvBlock",
                symbolType = NodeSymbolType.VALVE,
                defaultStatusEn = "24V DC Pilot Control",
                defaultStatusAr = "تحكم بإشارات 24 فولت من PLC",
                x = 440f, y = 440f,
                rating = "Festo / SMC 5/2 directional valves",
                cabinet = "Main Pneumatic Enclosure",
                schematicsPage = "P. 15",
                descriptionEn = "Electropneumatic pilot manifold commanded by Siemens PLC to actuate heavy vacuum gate valves.",
                descriptionAr = "مجموعة صمامات كهرونيوماتيكية تتلقى أوامر PLC لتحريك مكابس صمامات الفراغ."
            ),
            CircuitNode(
                id = "pneu_hivva1",
                labelEn = "High Vac Gate Valve Actuator",
                labelAr = "مكبس صمام الفراغ العالي HiVVa1",
                componentTag = "HiVVa1",
                symbolType = NodeSymbolType.PISTON,
                defaultStatusEn = "Double-Acting Pneumatic",
                defaultStatusAr = "أسطوانة نيوماتيكية مزدوجة الفعل",
                x = 640f, y = 360f,
                rating = "DN500 Gate Valve, 5 bar pilot",
                cabinet = "Above Diffusion Pump",
                schematicsPage = "P. 16",
                descriptionEn = "Pneumatic cylinder actuating the main DN500 high vacuum gate valve between diffusion pump and chamber.",
                descriptionAr = "أسطوانة نيوماتيكية تفتح صمام البوابة الضخم بين مضخة الانتشار وغرفة الفرن."
            ),
            CircuitNode(
                id = "pneu_lovva1",
                labelEn = "Roughing Valve Actuator",
                labelAr = "مكبس صمام التفريغ المنخفض LoVVa1",
                componentTag = "LoVVa1",
                symbolType = NodeSymbolType.PISTON,
                defaultStatusEn = "Roughing Line Pilot",
                defaultStatusAr = "مكبس خط السحب الابتدائي",
                x = 640f, y = 460f,
                rating = "DN160 Poppet Valve",
                cabinet = "Vacuum Pumping Line",
                schematicsPage = "P. 17",
                descriptionEn = "Pneumatic actuator opening direct roughing line from Roots pump to furnace chamber.",
                descriptionAr = "مكبس نيوماتيكي يفتح صمام السحب الأولي من مضخة روتس إلى الفرن."
            )
        ),
        connections = listOf(
            // Gas lines
            CircuitConnection("gas_argon_source", "gas_regulator", FlowType.GAS_HIGH_PRESSURE, "16 bar Supply", "خط 16 بار"),
            CircuitConnection("gas_regulator", "gas_fast_inflow_valve", FlowType.GAS_HIGH_PRESSURE, "6 bar Regulated", "6 بار منظم"),
            CircuitConnection("gas_fast_inflow_valve", "gas_vessel_chamber", FlowType.GAS_HIGH_PRESSURE, "Quench Blast", "تدفق التبريد"),
            CircuitConnection("gas_regulator", "gas_partial_valve", FlowType.GAS_HIGH_PRESSURE, "Micro-Feed", "تغذية دقيقة"),
            CircuitConnection("gas_partial_valve", "gas_vessel_chamber", FlowType.GAS_HIGH_PRESSURE, "Partial Pressure (0.1-1 torr)", "ضغط جزئي"),
            // Pneumatic lines
            CircuitConnection("pneu_air_supply", "pneu_buffer_tank", FlowType.PNEUMATIC_AIR, "Air Feed", "تغذية هواء"),
            CircuitConnection("pneu_buffer_tank", "pneu_vent_valve", FlowType.PNEUMATIC_AIR, "Drain / Dump", "تفريغ LOTO"),
            CircuitConnection("pneu_buffer_tank", "pneu_solenoid_manifold", FlowType.PNEUMATIC_AIR, "5-6 bar Pilot Air", "هواء تحكم 5-6 بار"),
            CircuitConnection("pneu_solenoid_manifold", "pneu_hivva1", FlowType.PNEUMATIC_AIR, "HiVVa Pilot", "إشارة فتح صمام الفراغ العالي"),
            CircuitConnection("pneu_solenoid_manifold", "pneu_lovva1", FlowType.PNEUMATIC_AIR, "LoVVa Pilot", "إشارة فتح صمام الفراغ المنخفض")
        ),
        traceablePaths = listOf(
            TraceablePath(
                id = "path_quenching_gas",
                nameEn = "6 bar Argon Rapid Quench Circuit",
                nameAr = "مسار التبريد الفجائي بغاز الآرجون 6 بار",
                descriptionEn = "Traces quenching gas flow from high-purity tank through regulator and fast inflow valve into chamber nozzles.",
                descriptionAr = "تتبع مسار غاز التبريد من الخزان عبر المنظم وصمام الدخول السريع حتى رشاشات الفرن.",
                nodeIds = listOf("gas_argon_source", "gas_regulator", "gas_fast_inflow_valve", "gas_vessel_chamber"),
                flowColorHex = 0xFF00E5FF
            ),
            TraceablePath(
                id = "path_pneumatic_gate_valves",
                nameEn = "Pneumatics & High Vacuum Valve Circuit",
                nameAr = "مسار التحكم النيوماتيكي بصمام الفراغ العالي",
                descriptionEn = "Traces compressed air path from buffer tank through solenoid manifold to open high vacuum gate valve HiVVa1.",
                descriptionAr = "تتبع مسار الهواء المضغوط من خزان 50 لتر عبر الصمامات الكهرونيوماتيكية لفتح صمام الفراغ العالي.",
                nodeIds = listOf("pneu_air_supply", "pneu_buffer_tank", "pneu_solenoid_manifold", "pneu_hivva1"),
                flowColorHex = 0xFFFFC107
            ),
            TraceablePath(
                id = "path_partial_pressure",
                nameEn = "Partial Pressure Alloying Protection",
                nameAr = "مسار الضغط الجزئي لحماية السبائك",
                descriptionEn = "Traces micro-dosed argon gas line keeping partial pressure between 0.1 and 1.0 torr to suppress metal evaporation.",
                descriptionAr = "تتبع خط حقن الآرجون الميكرومتري لمنع تبخر عناصر السبيكة كالتيتانيوم والكروم.",
                nodeIds = listOf("gas_argon_source", "gas_regulator", "gas_partial_valve", "gas_vessel_chamber"),
                flowColorHex = 0xFF4CAF50
            )
        )
    )

    // =========================================================================
    // 4. HYDRAULIC & MECHANICAL DOOR LOCKING SCHEMATICS
    // =========================================================================
    val hydraulicDoorDiagram = CircuitDiagram(
        id = "circuit_hydraulic_door",
        domain = CircuitDomain.HYDRAULIC_DOOR,
        titleEn = "Hydraulic & Mechanical Door Locking",
        titleAr = "المخطط الهيدروليكي والميكانيكي لإحكام أمان الباب",
        subtitleEn = "Pilz Two-Hand Remote (ACT1+ACT2), Locking Rings, Safety Piston & Manual Latch",
        subtitleAr = "محطة بيلز باليدين (ACT1+ACT2)، حلقات الكبس، مكبس الأمان والمزلاج اليدوي",
        schematicsRef = "Operation Manual Doc. 911_man_eng_a01 P. 27-29 & P. 82",
        nodes = listOf(
            CircuitNode(
                id = "hyd_power_pack",
                labelEn = "Hydraulic Power Unit",
                labelAr = "محطة الضخ الهيدروليكية المركزية",
                componentTag = "HPU-1",
                symbolType = NodeSymbolType.POWER_SOURCE,
                defaultStatusEn = "Pressure 120 - 150 bar",
                defaultStatusAr = "ضغط الزيت 120 - 150 بار",
                x = 100f, y = 280f,
                rating = "4 kW motor, 150 bar, ISO VG 46 oil",
                cabinet = "Hydraulic Station Base",
                schematicsPage = "P. 70",
                descriptionEn = "Hydraulic pack providing pressurized oil to door movement cylinders and clamping mechanisms.",
                descriptionAr = "وحدة هيدروليكية توفر ضغط الزيت لتحريك وسحب أسطوانات الباب وحلقات القفل.",
                isPrimarySource = true
            ),
            CircuitNode(
                id = "hyd_pilz_two_hand",
                labelEn = "Pilz PIT js2 Two-Hand Station",
                labelAr = "جهاز التحكم باليدين Pilz PIT js2",
                componentTag = "ACT1+ACT2",
                symbolType = NodeSymbolType.SAFETY_STATION,
                defaultStatusEn = "Simultaneous Dual Press Required",
                defaultStatusAr = "يلزم ضغط الزرين معاً بالتزامن",
                x = 280f, y = 140f,
                rating = "EN 574 Type IIIC / Cat. 4 PL e",
                cabinet = "Yellow Mobile Safety Pendant",
                schematicsPage = "P. 72",
                descriptionEn = "Mandatory safety station requiring operator to hold both ACT1 and ACT2 pushbuttons with both hands to prevent crush/pinch hazard.",
                descriptionAr = "محطة أمان إلزامية تلزم المشغل بالضغط بكلتا يديه على ACT1 و ACT2 بالتزامن لمنع حوادث البتر والسحق.",
                relatedAlarms = listOf("Safety station ACT1/ACT2 discordance")
            ),
            CircuitNode(
                id = "hyd_safety_plc_interlock",
                labelEn = "Safety PLC Door Interlock",
                labelAr = "قفل أمان كمبيوتر Pilz PSSu",
                componentTag = "Pilz-Door",
                symbolType = NodeSymbolType.PLC_MODULE,
                defaultStatusEn = "Safety Interlock Healthy",
                defaultStatusAr = "شروط الأمان متحققة ومجازة",
                x = 460f, y = 140f,
                rating = "Dual-channel 24V failsafe output",
                cabinet = "Main Cabinet (MiCa)",
                schematicsPage = "P. 74",
                descriptionEn = "Permits door motion only when furnace temperature is below safe threshold (<90°C) and chamber is fully vented.",
                descriptionAr = "يسمح بحركة الباب فقط بعد انتهاء التبريد وهبوط الحرارة دون 90°م ومعادلة الضغط الجوي."
            ),
            CircuitNode(
                id = "hyd_directional_valve",
                labelEn = "Hydraulic Proportional Valve",
                labelAr = "الصمام الهيدروليكي التناسبي الموجه",
                componentTag = "Hv-Door",
                symbolType = NodeSymbolType.VALVE,
                defaultStatusEn = "Closed / Center Position",
                defaultStatusAr = "مغلق / وضع الصفر الآمن",
                x = 280f, y = 380f,
                rating = "Rexroth 4/3 directional, 24V DC solenoid",
                cabinet = "Hydraulic Valve Block",
                schematicsPage = "P. 75",
                descriptionEn = "Directs hydraulic oil flow to open or close door cylinders under safety supervision.",
                descriptionAr = "يوجه تدفق الزيت الهيدروليكي لأسطوانات الباب تحت إشراف كمبيوتر الأمان."
            ),
            CircuitNode(
                id = "hyd_translation_cylinder",
                labelEn = "Door Translation Cylinder",
                labelAr = "أسطوانة تحريك وسحب الباب",
                componentTag = "Cyl-Trans",
                symbolType = NodeSymbolType.PISTON,
                defaultStatusEn = "Closed & Homed",
                defaultStatusAr = "مغلق ومستقر في موضعه",
                x = 460f, y = 380f,
                rating = "Bore 80mm / Stroke 800mm",
                cabinet = "Door Rail Frame",
                schematicsPage = "P. 76",
                descriptionEn = "Translates the heavy vacuum door horizontally towards and away from the furnace vessel flange.",
                descriptionAr = "تحرك باب الفرن الضخم أفقياً للاقتراب من شفة الفرن أو الابتعاد عنها للتحميل."
            ),
            CircuitNode(
                id = "hyd_clamping_rings",
                labelEn = "Door Clamping Rings",
                labelAr = "حلقات كبس وإحكام الباب (Clamping Rings)",
                componentTag = "Rings-Lock",
                symbolType = NodeSymbolType.PISTON,
                defaultStatusEn = "Rotated / Fully Clamped",
                defaultStatusAr = "مستديرة ومحكمة الإغلاق",
                x = 660f, y = 280f,
                rating = "Radial interlocking bayonet ring",
                cabinet = "Front Vessel Flange",
                schematicsPage = "P. 78",
                descriptionEn = "Rotating locking rings that mechanically grab the door teeth with immense clamping force against elastomer O-ring.",
                descriptionAr = "حلقات دائرية مسننة تلف ميكانيكياً لتعشيق أسنان الباب وضغط الجوان لمنع أي تسريب فراغي.",
                relatedAlarms = listOf("Door clamping ring sensor missing")
            ),
            CircuitNode(
                id = "hyd_safety_piston",
                labelEn = "Mechanical Safety Pin Piston",
                labelAr = "مكبس قفل الأمان الميكانيكي (Safety Pin)",
                componentTag = "SafePist",
                symbolType = NodeSymbolType.PISTON,
                defaultStatusEn = "Extended / Pin Inserted",
                defaultStatusAr = "ممتد / المسمار معشق في حلقة القفل",
                x = 840f, y = 200f,
                rating = "Hardened steel lock pin + inductive switch",
                cabinet = "Door Ring Housing",
                schematicsPage = "P. 80",
                descriptionEn = "Pneumatic/hydraulic pin that physically blocks the clamping ring from rotating loose under internal pressure.",
                descriptionAr = "مسمار فولاذي صلب يمنع دوران حلقات القفل أثناء وجود ضغط داخلي في الفرن.",
                relatedAlarms = listOf("Safety piston error")
            ),
            CircuitNode(
                id = "hyd_manual_latch",
                labelEn = "Manual Safety Latch & Bleed",
                labelAr = "المزلاج اليدوي وصمام تفريغ الضغط",
                componentTag = "ManLatch",
                symbolType = NodeSymbolType.SAFETY_STATION,
                defaultStatusEn = "Latched / Sensor Engaged",
                defaultStatusAr = "مقفل يدوياً والحساس معشق",
                x = 840f, y = 380f,
                rating = "Mechanical lever + safety limit switch",
                cabinet = "Door Operator Station",
                schematicsPage = "P. 82",
                descriptionEn = "Hand-operated mechanical safety latch that mechanically secures safety piston and safely bleeds residual pressure before door can crack open.",
                descriptionAr = "مزلاج يدوي يؤمن مكبس الأمان ويصرف أي ضغط غازي متبقٍ قبل السماح بفتح الباب بالكامل.",
                relatedAlarms = listOf("Manual latch missing sensor", "Overpressure discharge valve")
            )
        ),
        connections = listOf(
            CircuitConnection("hyd_power_pack", "hyd_directional_valve", FlowType.HYDRAULIC_FLUID, "150 bar Pressure Line", "خط ضغط 150 بار"),
            CircuitConnection("hyd_pilz_two_hand", "hyd_safety_plc_interlock", FlowType.SAFETY_SIGNAL, "Dual ACT1+ACT2 Signal", "إشارة أمان ACT1+ACT2"),
            CircuitConnection("hyd_safety_plc_interlock", "hyd_directional_valve", FlowType.ELECTRIC_CONTROL, "24V Pilot Enable", "إذن فتح الصمام 24 فولت"),
            CircuitConnection("hyd_directional_valve", "hyd_translation_cylinder", FlowType.HYDRAULIC_FLUID, "Translation Motion", "حركة سحب الباب"),
            CircuitConnection("hyd_directional_valve", "hyd_clamping_rings", FlowType.HYDRAULIC_FLUID, "Ring Rotation Fluid", "تدوير حلقات الكبس"),
            CircuitConnection("hyd_clamping_rings", "hyd_safety_piston", FlowType.SAFETY_SIGNAL, "Ring Position Trigger", "تعشيق مكبس الأمان"),
            CircuitConnection("hyd_safety_piston", "hyd_manual_latch", FlowType.SAFETY_SIGNAL, "Pin Lock Security", "قفل المزلاج اليدوي")
        ),
        traceablePaths = listOf(
            TraceablePath(
                id = "path_door_close_sequence",
                nameEn = "Two-Hand Safety Door Closing Sequence",
                nameAr = "مسلسل إغلاق الباب الآمن باليدين ACT1+ACT2",
                descriptionEn = "Traces sequential safety steps from pressing ACT1+ACT2, through Pilz PLC, directional valve, cylinder translation to ring clamping.",
                descriptionAr = "تتبع تسلسل أمان إغلاق الباب: ضغط ACT1+ACT2 بالتزامن، إذن كمبيوتر الأمان، حركة الأسطوانة، ثم تدوير حلقات الكبس.",
                nodeIds = listOf("hyd_pilz_two_hand", "hyd_safety_plc_interlock", "hyd_directional_valve", "hyd_translation_cylinder", "hyd_clamping_rings"),
                flowColorHex = 0xFF4CAF50
            ),
            TraceablePath(
                id = "path_mechanical_locking",
                nameEn = "Ring Clamping & Safety Piston Lock",
                nameAr = "مسار إحكام الحلقات وقفل مكبس الأمان والمزلاج",
                descriptionEn = "Traces mechanical locking sequence from clamping rings rotation, safety pin insertion, to manual latch engagement.",
                descriptionAr = "تتبع مسار الأمان الميكانيكي من دوران الحلقات المسننة، نزول مكبس الأمان، إلى قفل المزلاج اليدوي.",
                nodeIds = listOf("hyd_power_pack", "hyd_clamping_rings", "hyd_safety_piston", "hyd_manual_latch"),
                flowColorHex = 0xFFFF9800
            )
        )
    )

    val allDiagrams: List<CircuitDiagram> = listOf(
        electricalDiagram,
        waterDiagram,
        gasPneumaticDiagram,
        hydraulicDoorDiagram
    )

    fun getDiagramByDomain(domain: CircuitDomain): CircuitDiagram {
        return allDiagrams.firstOrNull { it.domain == domain } ?: electricalDiagram
    }
}
