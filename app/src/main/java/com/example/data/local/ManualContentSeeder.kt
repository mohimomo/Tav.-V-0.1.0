package com.example.data.local

object ManualContentSeeder {
    fun getInitialManualDocuments(): List<ManualDocumentEntity> {
        return listOf(
            ManualDocumentEntity(
                id = "doc_ch1_cover",
                pageNumber = 1,
                chapterNumber = "1",
                chapterTitleEn = "General Introduction",
                chapterTitleAr = "المقدمة العامة ومعلومات الفرن",
                sectionNumber = "1.0",
                sectionTitleEn = "Operation and Maintenance Manual Cover",
                sectionTitleAr = "غلاف دليل التشغيل والصيانة الرسمي",
                contentEn = """
                    TAV VACUUM FURNACES
                    OPERATION AND MAINTENANCE MANUAL
                    Furnace Model: TPH 120/120/230
                    Serial Number: 911
                    Year of construction: 2023
                    ORIGINAL INSTRUCTIONS 01.2023
                    Written in accordance with paragraph 1.7.4 of the Essential Requirements of Machinery Directive Att. I 2006/42/CE.
                """.trimIndent(),
                contentAr = """
                    شركة TAV للأفران الفراغية
                    دليل التشغيل والصيانة الرسمي
                    طراز الفرن: TPH 120/120/230
                    الرقم التسلسلي: 911
                    سنة الصنع: 2023
                    التعليمات الأصلية 01.2023
                    كُتبت وفقاً للفقرة 1.7.4 من المتطلبات الأساسية لتوجيه الآلات الأوروبي 2006/42/CE.
                """.trimIndent(),
                keyPointsEn = "Model TPH 120/120/230 • Serial 911 • Year 2023 • Directive 2006/42/CE",
                keyPointsAr = "طراز TPH 120/120/230 • الرقم التسلسلي 911 • سنة الصنع 2023",
                category = "GENERAL",
                rawPdfOcrExcerpt = "1/89 OPERATION AND MAINTENANCE MANUAL Furnace Model: TPH 120/120/230 Serial Number: 911 Year of construction: 2023"
            ),
            ManualDocumentEntity(
                id = "doc_ch1_technical_data",
                pageNumber = 5,
                chapterNumber = "1",
                chapterTitleEn = "General Introduction",
                chapterTitleAr = "المقدمة العامة ومعلومات الفرن",
                sectionNumber = "1.3",
                sectionTitleEn = "Technical Data & Furnace Specifications",
                sectionTitleAr = "البيانات الفنية ومواصفات أداء الفرن",
                contentEn = """
                    DIMENSIONS:
                    • Working useful space: Width 1200 mm, Height 1200 mm, Depth 2300 mm
                    • Working useful volume: 3312 dm³ (3.31 m³)
                    
                    TEMPERATURE:
                    • Maximum operating temperature (cleaning cycle/baking cycle): 1300 °C
                    • Standard operating temperature: 1280 °C
                    • Temperature uniformity in vacuum (600°C < T < 1200°C): ± 5 °C
                    
                    VACUUM:
                    • Ultimate vacuum: 10⁻⁶ torr
                    • Operating vacuum: 10⁻⁴ / 10⁻⁵ torr
                    • Partial pressure (Ar): 0.1 – 1.0 torr
                    • Leak rate: < 25 micron/h
                    
                    FURNACE PERFORMANCES:
                    • Pumping time to 1×10⁻² mbar range: < 15 min
                    • Cooling time of furnace from 1200°C to 400°C: < 20 min
                    • Heating time from room temp to 1200°C (empty furnace): < 60 min
                    
                    CHARGE CAPACITY & ENERGY:
                    • Maximum gross charge: 2000 kg
                    • Heating power: 480 kW
                    • Connected load: 750 kVA
                    • Rated voltage: 400 V, 3 phase, 50 Hz
                """.trimIndent(),
                contentAr = """
                    الأبعاد وحجم التشغيل:
                    • المساحة المفيدة للتشغيل: العرض 1200 مم، الارتفاع 1200 مم، العمق 2300 مم.
                    • الحجم المفيد للتشغيل: 3312 دسم³ (3.31 متر مكعب).
                    
                    درجات الحرارة:
                    • أقصى حرارة تشغيل (دورة الخَبز والتنظيف): 1300 درجة مئوية.
                    • حرارة التشغيل القياسية: 1280 درجة مئوية.
                    • تجانس الحرارة في الفراغ (600 - 1200°م): ± 5 درجات مئوية.
                    
                    مستويات التفريغ الهوائي (Vacuum):
                    • أقصى تفريغ نهائي: 10⁻⁶ تور (Torr).
                    • تفريغ التشغيل القياسي: 10⁻⁴ إلى 10⁻⁵ تور.
                    • الضغط الجزئي لغاز الآرجون: 0.1 إلى 1.0 تور.
                    • أقصى معدل تسريب مسموح: أقل من 25 ميكرون/ساعة.
                    
                    معدلات الأداء:
                    • زمن السحب حتى نطاق 0.01 ميلي بار: أقل من 15 دقيقة.
                    • زمن التبريد من 1200°م إلى 400°م: أقل من 20 دقيقة.
                    • زمن التسخين من حرارة الغرفة إلى 1200°م (فارغ): أقل من 60 دقيقة.
                    
                    الحمولة والطاقة:
                    • أقصى حمولة إجمالية للشحنة: 2000 كجم.
                    • قدرة التسخين: 480 كيلوواط.
                    • إجمالي الحمل المتصل: 750 ك.ف.أ (kVA).
                    • الجهد الكهربائي المقنن: 400 فولت، 3 أطوار، 50 هرتز.
                """.trimIndent(),
                keyPointsEn = "1200x1200x2300mm • 3312 dm³ • Max 1300°C • Vac 10⁻⁶ torr • 480 kW • 2000 kg max load",
                keyPointsAr = "أبعاد 1200×1200×2300 مم • 3312 دسم³ • حرارة 1300°م • فراغ 10⁻⁶ تور • قدرة 480 ك.و • حمولة 2000 كجم",
                category = "SPECIFICATIONS",
                rawPdfOcrExcerpt = "Working useful space Width 1200 mm Height 1200 mm Depth 2300 mm Volume 3312 dm3 Max temp 1300 C Operating 1280 C Heating power 480 kW Connected load 750 kVA Max gross charge 2000 kg"
            ),
            ManualDocumentEntity(
                id = "doc_ch1_pumping_control_data",
                pageNumber = 6,
                chapterNumber = "1",
                chapterTitleEn = "General Introduction",
                chapterTitleAr = "المقدمة العامة ومعلومات الفرن",
                sectionNumber = "1.3.1",
                sectionTitleEn = "Pumping System, Gas, Control & Water Data Table",
                sectionTitleAr = "جدول مواصفات مضخات التفريغ، الغاز، الأتمتة والمياه",
                contentEn = """
                    PUMPING SYSTEM:
                    • Mechanical Pump: No. 2 Leybold Sogevac SV630B, 15 kW, 700 m³/h, 72-75 dB(A), Gearbox oil LVO130.
                    • Roots Pump: No. 1 Leybold Ruvac WH4400, 11 kW, 4400 m³/h, <60 dB(A), Fluid LVO210.
                    • Diffusion Pump: No. 1 Varian/Agilent NHS 35, Air speed 50,000 l/s, Heating 24 kW, Fluid DV747.
                    • Holding Pump: No. 1 Leybold Trivac D16B, 750 W, 16 m³/h, 52 dB(A), Oil Leybonol LVO 100.
                    
                    GAS SYSTEM:
                    • Quenching gas maximum pressure (Argon): 6 bar abs.
                    • Quenching gas purity: Better than 99.999%.
                    
                    CONTROL & AUTOMATION HARDWARE:
                    • Main PLC: Siemens S7-1513
                    • Safety PLC: PILZ PSS 4000
                    • SCADA: Siemens WinCC on Phoenix Contact Industrial PC
                    • Sensor bus: AS-Interface (IFM)
                    • Data Recorder: Eurotherm 6180A
                    • Overtemperature Instrument: Eurotherm EPC3016
                    • Vacuum gauges: Pfeiffer TPR270 (Pirani), Pfeiffer IKR251 (Penning)
                    • Pressure sensors: GE Druck PTX5072, IFM PN2094
                    • Temperature sensors: IFM TAA131
                    
                    WATER COOLING & WEIGHT:
                    • Cooling water pressure at inlet: 2.5 ÷ 3.0 bar
                    • Cooling water pressure at outlet: 0 ÷ 0.5 bar
                    • Chamber cooling water request: 900 l/min
                    • Heat exchanger request during quenching: 900 l/min
                    • Maximum water inlet temperature: 27 °C
                    • Maximum water hardness: 15 Fr° (150 ppm CaCO₃)
                    • Total plant gross weight: Approx. 34 tonnes.
                """.trimIndent(),
                contentAr = """
                    مضخات نظام التفريغ:
                    • المضخة الميكانيكية: عدد 2 Leybold Sogevac SV630B قدرة 15 ك.و، تدفق 700 م³/ساعة، زيت LVO130.
                    • مضخة روتس: عدد 1 Leybold Ruvac WH4400 قدرة 11 ك.و، تدفق 4400 م³/ساعة، زيت LVO210.
                    • مضخة الانتشار: عدد 1 Varian NHS 35 سرعة سحب 50,000 لتر/ث، قدرة تسخين 24 ك.و، زيت سيليكون DV747.
                    • مضخة الاستبقاء: عدد 1 Leybold Trivac D16B قدرة 750 واط، تدفق 16 م³/ساعة، زيت LVO 100.
                    
                    نظام الغاز والتبريد الفجائي:
                    • أقصى ضغط لغاز التبريد (الآرجون): 6 بار مطلق.
                    • نقاوة غاز التبريد: أعلى من 99.999%.
                    
                    أجهزة الأتمتة والتحكم:
                    • الـ PLC الرئيسي: Siemens S7-1513.
                    • الـ PLC الآمن: Pilz PSS 4000.
                    • شاشة SCADA: Siemens WinCC على حاسب صناعي Phoenix Contact.
                    • ناقل الحساسات: IFM AS-Interface.
                    • مسجل البيانات: Eurotherm 6180A.
                    • جهاز فرط الحرارة المستقل: Eurotherm EPC3016.
                    • حساسات الفراغ: Pfeiffer TPR270 و IKR251.
                    
                    تبريد المياه والوزن الإجمالي:
                    • ضغط الدخول لمياه التبريد: 2.5 - 3.0 بار.
                    • استهلاك مياه تبريد الغرفة: 900 لتر/دقيقة.
                    • استهلاك المبادل الحراري أثناء التبريد: 900 لتر/دقيقة.
                    • أقصى حرارة دخول للمياه: 27 درجة مئوية.
                    • أقصى عسورة مياه: 15 درجة فرنسية (150 ملغم/لتر CaCO₃).
                    • الوزن الإجمالي للفرن: حوالي 34 طن.
                """.trimIndent(),
                keyPointsEn = "SV630B (15kW) • WH4400 (11kW) • NHS 35 (50,000 l/s) • Siemens S7-1513 • Pilz PSS • 34 tonnes",
                keyPointsAr = "مضخات SV630B و WH4400 و NHS 35 • سيمنز S7-1513 • بيلز PSS • وزن 34 طن",
                category = "SPECIFICATIONS",
                rawPdfOcrExcerpt = "Mechanical pump Leybold SV630 Roots Leybold WH4400 Diffusion NHS 35 PLC Siemens S7-1513 Safety PILZ PSS Quenching gas Ar 6 bar abs Plant gross weight Approx 34"
            ),
            ManualDocumentEntity(
                id = "doc_ch2_installation_water",
                pageNumber = 7,
                chapterNumber = "2",
                chapterTitleEn = "Installation Requirements",
                chapterTitleAr = "متطلبات التركيب والموقع",
                sectionNumber = "2.3",
                sectionTitleEn = "Ground, Electrical, Emergency Water & Water Quality",
                sectionTitleAr = "متطلبات الأرضية، التوصيل الكهربائي، مياه الطوارئ وجودة المياه",
                contentEn = """
                    2.1 GROUND REQUIREMENTS:
                    • Ground must bear total plant weight (34 tonnes) + max charge (2000 kg).
                    • Recommended flatness: 0.2% (2 mm per meter).
                    
                    2.2 ELECTRICAL CONNECTION:
                    • Power cabinet specifications as in Technical Data. Must be tested according to EN 60204-1 before startup.
                    
                    2.3.1 EMERGENCY WATER SUPPLY (Gas-Driven Pump):
                    • Gas connection for emergency pump: 1/2".
                    • Required gas supply: 30 Nm³/h at 5 barG.
                    • In case of blackout or water system failure, emergency gas pump starts automatically to protect furnace from severe damage.
                    • Emergency water circuit availability checked automatically at every start cycle. All manual valves on emergency water pipe must ALWAYS remain open.
                    
                    2.3.2 WATER QUALITY SPECIFICATIONS (Table 2):
                    • Aspect: Clear, colorless, odorless, absence of suspended solids.
                    • Distilled water must NEVER be used (too aggressive/corrosive).
                    • pH: > 7 (recommended range: 7.0 to 8.0).
                    • Conductivity: 500 – 1500 µS/cm.
                    • TDS: 1500 mg/l at 180°C.
                    • Total Hardness: 100 - 150 mg/l CaCO₃ (10 - 15 FR° / 5.6 - 8.4 dH).
                    • Chloride: < 80 mg/l.
                    • Silica: < 60 mg/l SiO₂.
                    • Fe + Mn: < 1 mg/l.
                    • Ammonia (NH3), H2S, Organic compounds: Absent.
                """.trimIndent(),
                contentAr = """
                    2.1 متطلبات الأرضية:
                    • يجب أن تتحمل الأرضية وزن الفرن (34 طن) بالإضافة لأقصى حمولة (2 طن).
                    • الاستواء الموصى به: 0.2% (2 مم لكل متر).
                    
                    2.2 التوصيل الكهربائي:
                    • يجب فحص واختبار التوصيلات الكهربائية وفق المعيار الأوروبي EN 60204-1 قبل بدء التشغيل.
                    
                    2.3.1 إمداد مياه الطوارئ بمضخة الغاز المضغوط:
                    • وصلة تغذية الغاز لمضخة الطوارئ: 1/2 بوصة.
                    • كمية الغاز المطلوبة: 30 متر مكعب قياسي/ساعة عند ضغط 5 بار.
                    • في حال انقطاع الكهرباء أو تعطل مضخات المياه الرئيسية، تبدأ مضخة الغاز تلقائياً لتدوير مياه التبريد وحماية الفرن من التلف والذوبان.
                    • يجب أن تظل جميع المحابس اليدوية على خط مياه الطوارئ مفتوحة دائماً وأبداً.
                    
                    2.3.2 مواصفات جودة المياه (جدول 2):
                    • المظهر: صافٍ، عديم اللون والرائحة، وخالٍ من الرواسب العالقة.
                    • يُمنع منعاً باتاً استخدام الماء المقطر (لأنه يسبب تآكلاً شديداً للمعادن).
                    • درجة الحموضة pH: أعلى من 7 (المجال الموصى به بين 7 و 8).
                    • الناقلية الكهربائية: 500 إلى 1500 ميكروسيمنز/سم.
                    • العسورة الكلية: 10 إلى 15 درجة فرنسية (100 - 150 ملغم/لتر كربونات الكالسيوم).
                    • الكلوريد: أقل من 80 ملغم/لتر.
                    • السيليكا: أقل من 60 ملغم/لتر.
                    • الحديد والمنغنيز: أقل من 1 ملغم/لتر.
                """.trimIndent(),
                keyPointsEn = "Ground 0.2% • Emergency gas pump 30 Nm³/h @ 5barG • Never use distilled water • Hardness 10-15 FR° • pH 7-8",
                keyPointsAr = "استواء 0.2% • مضخة طوارئ بالغاز 30 م³/س عند 5 بار • منع الماء المقطر • عسورة 10-15 درجة فرنسية",
                category = "SPECIFICATIONS",
                rawPdfOcrExcerpt = "Ground requirements bear loads 34t + 2000kg. Emergency water supply 30 Nm3/h at 5barG. Water quality distilled water must not be used pH 7 to 8 Hardness 10 - 15 FR"
            ),
            ManualDocumentEntity(
                id = "doc_ch3_safety_notes",
                pageNumber = 10,
                chapterNumber = "3",
                chapterTitleEn = "Safety Notes",
                chapterTitleAr = "ملاحظات وتعليمات السلامة والأمان",
                sectionNumber = "3.1 - 3.7",
                sectionTitleEn = "Asphyxiation, High Temperature, Electrical & Chemical Hazards",
                sectionTitleAr = "مخاطر الاختناق، الحرارة العالية، الكهرباء وانفجار مضخة الانتشار",
                contentEn = """
                    3.1 GENERAL SAFETY:
                    • Calculations according to EN 13849-1 based on 4 cycles/day, 365 days/year for 20 years.
                    • Only trained operators are permitted to work on the furnace.
                    
                    3.2 RISK OF ASPHYXIATION:
                    • Argon and Nitrogen quenching gases displace oxygen rapidly. Never enter vessel without forced ventilation and verified oxygen sensors.
                    
                    3.3 HIGH TEMPERATURE RISKS:
                    • Compulsory thermal gloves and glasses at end of cycle when door is opened.
                    • Vessel must be water cooled at all times. Thermo-switch on vessel cuts heating power if vessel overheats.
                    • Diffusion pump boiler operates at ~230°C. Never touch lower pump body.
                    
                    3.5 ELECTRICAL RISKS:
                    • DANGER: Orange wires inside control cabinet remain energized EVEN WHEN MAIN DISCONNECT SWITCH IS OFF! Shut off power at upstream building service disconnect.
                    • Magnetic fields: People with pacemakers must keep away due to high heating currents.
                    
                    3.6.6 FLAMMABLE SUBSTANCES & DIFFUSION PUMP EXPLOSION RISK:
                    • Diffusion pump uses silicon oil (DV747) at high temperature (~230°C).
                    • CRITICAL WARNING: Venting diffusion pump while hot will cause catastrophic explosion of the pump itself! Interlocks prevent opening valves to air while hot.
                    
                    3.6.7 RADIATIONS & FAN DANGER:
                    • Furnace cannot open above 180°C (standard air admit threshold is 90°C).
                    • Testing cooling fan with open door requires protective goggles against high air blasts and dust.
                """.trimIndent(),
                contentAr = """
                    3.1 أمان عام:
                    • مصمم لحسابات أمان 4 دورات يومياً لمدة 20 سنة وفق معيار EN 13849-1.
                    • التشغيل والصيانة حصراً للموظفين المدربين والمصرح لهم.
                    
                    3.2 خطر الاختناق بالغازات:
                    • غازا الآرجون والنيتروجين المستخدمان في التبريد يزيحان الأكسجين دون رائحة. يُحظر الدخول داخل الفرن دون تهوية والتأكد من نسبة الأكسجين.
                    
                    3.3 مخاطر درجات الحرارة العالية:
                    • ارتداء القفازات الحرارية والنظارات إلزامي عند فتح الباب بعد انتهاء الدورة.
                    • تبريد جدار الفرن بالماء إجباري طوال فترة التشغيل، ويوجد حساس حراري يقطع التسخين فوراً في حال سخونة الجدار الخارجي.
                    
                    3.5 المخاطر الكهربائية:
                    • تحذير خطير: الأسلاك البرتقالية (Orange Wires) داخل لوحة التحكم تبقى تحت الجهد الكهربائي حتى عند إطفاء القاطع الرئيسي للوحة! لإجراء الصيانة على هذه الدوائر يجب فصل القاطع العام المغذي من المصنع.
                    • الحقول الكهرومغناطيسية: يُمنع اقتراب حاملي أجهزة تنظيم ضربات القلب (Pacemakers) من الفرن بسبب التيارات الضخمة المارة بعناصر التسخين (480 كيلوواط).
                    
                    3.6.6 خطر انفجار مضخة الانتشار:
                    • تعمل مضخة الانتشار بزيت السيليكون تحت حرارة 230 درجة مئوية.
                    • تحذير قاتل: فتح مضخة الانتشار للهواء وهي ساخنة يؤدي لانفجار المضخة فوراً! نظومة الأمان تمنع فتح الصمامات للهواء وهي ساخنة.
                """.trimIndent(),
                keyPointsEn = "Asphyxiation risk • Orange wires stay LIVE with main switch off • Hot diffusion pump explosion risk (DV747)",
                keyPointsAr = "خطر الاختناق • الأسلاك البرتقالية تبقى مكهربة • خطر انفجار مضخة الانتشار إذا فُتحت وهي ساخنة",
                category = "SAFETY",
                rawPdfOcrExcerpt = "Orange wires remain energized even when main switch is in OFF position. Venting diffusion pump when still hot will cause explosion of the pump itself. Gloves compulsory."
            ),
            ManualDocumentEntity(
                id = "doc_ch4_vacuum_pumps_valves",
                pageNumber = 14,
                chapterNumber = "4",
                chapterTitleEn = "Furnace Description",
                chapterTitleAr = "وصف أجزاء الفرن والمكونات",
                sectionNumber = "4.1 - 4.7",
                sectionTitleEn = "Vacuum System, Pneumatics, Cabinets & Two-Hand Door Control",
                sectionTitleAr = "منظومة التفريغ، النيوماتيك، اللوحات الكهربائية ومحطة أمان الباب",
                contentEn = """
                    4.1 VACUUM PUMPING SYSTEM:
                    • Mechanical Pump [MePu1, MePu2]: Leybold SV630B evacuates from atmosphere to base roughing vacuum. Must run during whole cycle.
                    • Roots Pump [RoPu1]: Leybold WH4400 boosts evacuation speed once roughing vacuum is achieved and Low Vacuum Valve is open.
                    • Diffusion Pump [DiPu1]: Varian NHS 35 reaches high vacuum 10⁻⁶ torr. Oil boiler heated to 230°C.
                    • Holding Pump [HoPu1]: Leybold D16B keeps diffusion pump under continuous vacuum when high vacuum valve is closed.
                    • High Vacuum Valve [HiVVa1]: Gate valve between diffusion pump and vessel. Opens only when working temperature and matching vacuum are verified.
                    • Low Vacuum Valve [LoVVa1]: Roughing valve between Roots pump and vessel.
                    • Foreline Valve [FoVa1]: Backing valve connecting diffusion pump to mechanical backing pump.
                    • Air Inlet Valve [AiInVa]: Admits air to vent chamber (<90°C only).
                    
                    4.3 PNEUMATIC SYSTEMS:
                    • 50 Liters buffer tank feeds all pneumatic cylinders and valves. Uses inert gas to prevent piston oxidation.
                    
                    4.4 ELECTRICAL CABINETS & PLC:
                    • Main Cabinet (MiCa): Light stack (Red: Alarm, Blue: Overpressure, Green: In cycle), Main isolator, Emergency stop, Eurotherm EPC3016 overtemp controller, Ammeters/voltmeters, Siemens IPC477E Industrial PC.
                    • Main PLC: Siemens S7-1513 managing sequences and analog PID.
                    • Safety PLC: Pilz PSSu redundant dual-microprocessor architecture for failsafe interlocks.
                    
                    4.7 DOOR CONTROL STATIONS (Two-Hand Control):
                    • Door open/close selector + Door enable lamp.
                    • Pilz PIT js2 pendant station with ACT1 and ACT2 pushbuttons. Operator MUST hold down ACT1 and ACT2 simultaneously with both hands to enable door movement.
                """.trimIndent(),
                contentAr = """
                    4.1 منظومة مضخات وصمامات الفراغ:
                    • المضخات الميكانيكية [MePu1, MePu2]: طراز Leybold SV630B لتفريغ الغرفة من الضغط الجوي حتى الفراغ الأولي، وتعمل طوال الدورة.
                    • مضخة روتس [RoPu1]: طراز Leybold WH4400 لتعزيز سرعة الشفط، تبدأ بعد تحقيق الفراغ التمهيدي وفتح صمام التفريغ المنخفض LoVVa1.
                    • مضخة الانتشار [DiPu1]: طراز Varian NHS 35 لتحقيق الفراغ العالي (10⁻⁶ تور)، تُسخن غلاية الزيت حتى 230°م.
                    • مضخة الاستبقاء [HoPu1]: طراز Leybold D16B لإبقاء مضخة الانتشار مفرغة عندما يكون صمام الفراغ العالي مغلقاً.
                    • صمام الفراغ العالي [HiVVa1]: صمام بوابة كبير، لا يفتح إلا بعد جاهزية مضخة الانتشار وتساوي الضغط.
                    • صمام الفراغ المنخفض [LoVVa1]: صمام خط الشفط الابتدائي من مضخة روتس إلى الفرن.
                    • صمام تنفيس الهواء [AiInVa]: لدخول الهواء ومعادلة الضغط قبل فتح الباب (يشترط حرارة أقل من 90°م).
                    
                    4.3 المنظومة النيوماتيكية:
                    • خزان هواء سعة 50 لتراً لتغذية جميع أسطوانات ومكابس الصمامات بالغاز الخامل لحماية الجوانات من الأكسدة.
                    
                    4.4 اللوحات الكهربائية والتحكم:
                    • اللوحة الرئيسية (MiCa): برج الإشارة الضوئي (أحمر: إنذار، أزرق: ضغط زائد، أخضر: دورة جارية)، قاطع رئيسي، زر طوارئ، منظم فرط الحرارة المستقل Eurotherm EPC3016، ومقاييس الأمبير والفولت.
                    • كمبيوتر التحكم PLC: سيمنز S7-1513.
                    • كمبيوتر الأمان: Pilz PSSu ذو معالجين متطابقين لمراقبة إشارات الأمان الحيوية.
                    
                    4.7 محطة أمان تحريك الباب:
                    • ريموت التحكم Pilz PIT js2 المزود بزري ACT1 و ACT2. يلزم ضغط الزرين معاً بكلتا اليدين بالتزامن لتحريك مكبس وحلقات الباب ومنع حوادث البتر.
                """.trimIndent(),
                keyPointsEn = "Leybold SV630B • Ruvac WH4400 • NHS 35 • S7-1513 PLC • Pilz PSSu Safety • Two-hand ACT1+ACT2 control",
                keyPointsAr = "مضخات SV630B و WH4400 و NHS 35 • سيمنز S7-1513 • بيلز PSSu • تحكم الباب باليدين ACT1+ACT2",
                category = "DESCRIPTION",
                rawPdfOcrExcerpt = "Mechanical pump Leybold SV630 Roots Leybold WH4400 Diffusion NHS 35 High vacuum valve HiVVa1 Low vacuum valve LoVVa1 Buffer tank 50L Siemens S7-1513 Safety Pilz PSSu Door control ACT1/ACT2"
            ),
            ManualDocumentEntity(
                id = "doc_ch5_software_wincc",
                pageNumber = 20,
                chapterNumber = "5",
                chapterTitleEn = "Software Description",
                chapterTitleAr = "دليل برنامج التشغيل (Siemens WinCC SCADA)",
                sectionNumber = "5.1 - 5.8",
                sectionTitleEn = "Recipes, Events, Settings, Programmer & Mimic",
                sectionTitleAr = "شاشات الوصفات، أحداث الدورة، الإعدادات، والمخطط التفاعلي Mimic",
                contentEn = """
                    5.1 RECIPES & EVENTS:
                    • PARTIAL PRESSURE: Admits tiny inert gas flow via micrometric valve to suppress alloy vaporization (e.g. chromium).
                    • FAN COOLING: Rapid cooling by circulating pressurized gas with centrifugal fan (PID controlled speed).
                    • ONLY GAS COOLING: Slower cooling with static gas backfill without running fan (up to 1 bar).
                    • LOAD INTERLOCK: Automatically halts soaking timer until load thermocouples enter tolerance band.
                    • FURNACE INTERLOCK: Halts timer until control thermocouple enters setpoint tolerance.
                    • LEAK RATE: Runs automatic leak rate check, logged to database.
                    • VACUUM COOLING: Natural cooling under vacuum without inert gas backfill.
                    
                    5.2 SETTINGS:
                    • Air Admit Temperature: 90 °C (Never set higher to avoid graphite insulation burn).
                    • Overtemperature: Maximum safety temperature inside chamber.
                    • Vessel Overtemperature: Max threshold on external jacket.
                    • High Vacuum Valve Opening Set: Vacuum ON threshold required before HiVVa opens.
                    • Diffusion pump working temp: ~230 °C.
                    
                    5.4 MIMIC & MANUAL SWITCHES:
                    • Graphic overview of all furnace pumps, valves, and temperatures with manual override capability.
                """.trimIndent(),
                contentAr = """
                    5.1 شاشة الوصفات وأحداث المعالجة (Events):
                    • الضغط الجزئي (Partial Pressure): إدخال تدفق خفيف من الغاز الخامل لمنع تطاير وتبخر عناصر السبيكة (مثل الكروم).
                    • تبريد المروحة الفجائي (Fan Cooling): تبريد فائق السرعة عبر ضخ الغاز وتدويره بمروحة الطرد المركزي بقوة 160 ك.و.
                    • تبريد بالغاز فقط (Only Gas Cooling): تبريد أبطأ بملء الفرن بالغاز دون تشغيل المروحة (حتى ضغط 1 بار).
                    • قفل الشحنة (Load Interlock): إيقاف عداد فترة التثبيت الحراري (Soaking) حتى تصل مزدوجات الشحنة إلى درجة الحرارة المقررة.
                    • فحص التسريب (Leak Rate): إجراء اختبار التسريب الآلي وتوثيق النتيجة في قاعدة البيانات.
                    • تبريد في الفراغ (Vacuum Cooling): تبريد طبيعي داخل الفراغ دون ضخ غاز خامل.
                    
                    5.2 إعدادات السلامة (Settings):
                    • حرارة السماح بدخول الهواء (Air Admit): 90 درجة مئوية كحد أقصى (تعديلها لأعلى يتلف العوازل الكربونية فوراً).
                    • حد فرط الحرارة (Overtemperature): أقصى حرارة مسموحة للفرن.
                    • حرارة عمل مضخة الانتشار: 230 درجة مئوية.
                    
                    5.4 شاشة المخطط الرسومي التفاعلي (Mimic):
                    • رسم توضيحي لكافة الصمامات والمضخات ومسارات المياه والغاز مع مفاتيح التحكم اليدوي المشروط بالأمان.
                """.trimIndent(),
                keyPointsEn = "WinCC SCADA • Events: Partial pressure, Fan cooling, Load interlock • Air admit max 90°C • Mimic screen",
                keyPointsAr = "برنامج WinCC SCADA • أحداث الدورة: تبريد بالمروحة، ضغط جزئي، قفل الشحنة • أقصى حرارة لفتح الهواء 90°م",
                category = "SOFTWARE",
                rawPdfOcrExcerpt = "Siemens WINCC Recipe Events Partial pressure Fan cooling Load interlock Leak rate Air admit temperature 90 C Overtemperature Mimic Manual switches"
            ),
            ManualDocumentEntity(
                id = "doc_ch6_furnace_operation",
                pageNumber = 27,
                chapterNumber = "6",
                chapterTitleEn = "Furnace Operation",
                chapterTitleAr = "إجراءات التشغيل اليومية",
                sectionNumber = "6.1 - 6.4",
                sectionTitleEn = "Startup, Shutdown, Door Interlocks & Loading Safety",
                sectionTitleAr = "خطوات البدء والإيقاف، إحكام الباب وشحن الفرن بالرافعة الشوكية",
                contentEn = """
                    6.2.1 FURNACE STARTUP SEQUENCE:
                    1. Ensure no maintenance is active on the furnace or auxiliary equipment.
                    2. Check that all manual valves on emergency water supply are OPEN and water is available.
                    3. Ensure water tank refill manual valves are open and refill sensor works.
                    4. Switch ON the Main Isolator on electrical cabinet.
                    5. Ensure no alarms are active on SCADA, then press Auxiliary Circuits Enable.
                    
                    6.2.2 FURNACE SHUTDOWN SEQUENCE:
                    1. Check internal furnace temperature is below 100 °C.
                    2. Close door and pump down to at least 1×10⁻¹ mbar to prevent contamination of thermal chamber.
                    3. Close manual valve on emergency water line to prevent unnecessary water/gas waste.
                    4. Switch off auxiliary circuits, then turn OFF the Main Isolator.
                    
                    6.4.1 DOOR CLOSING PROCEDURE:
                    • Slide door toward furnace until hook releases.
                    • Turn selector to "CLOSE DOOR". When DOOR ENABLE LAMP illuminates:
                    • Press and hold simultaneously ACT1 and ACT2 pushbuttons on remote control until rings and safety piston close in sequence.
                    • Lock the manual latch to secure the safety piston.
                    
                    6.4.2 DOOR OPENING PROCEDURE:
                    • Verify cycle is complete and cooling finished (<90°C).
                    • Open manual latch to discharge overpressure.
                    • Turn selector to "OPEN DOOR", press ACT1 and ACT2 simultaneously to retract safety piston and open rings.
                    • Pull door manually until locked in open position. Load/unload with forklift.
                """.trimIndent(),
                contentAr = """
                    6.2.1 خطوات بدء تشغيل الفرن اليومي:
                    1. التأكد من انتهاء جميع أعمال الصيانة على الفرن ومعداته.
                    2. التأكد من فتح جميع المحابس اليدوية على خط مياه الطوارئ وتوفر المياه وضغط الغاز.
                    3. فحص فتح محابس خزان المياه والتأكد من سلامة حساس الملء.
                    4. تشغيل القاطع الكهربائي الرئيسي (Main Isolator ON).
                    5. التأكد من خلو شاشة WinCC من الإنذارات، ثم ضغط زر تشغيل الدوائر المساعدة.
                    
                    6.2.2 خطوات إيقاف الفرن:
                    1. التأكد أن درجة حرارة الفرن أقل من 100 درجة مئوية تماماً.
                    2. إغلاق الباب وسحب الهواء حتى 0.1 م.بار لمنع دخول الرطوبة وتلوث العوازل.
                    3. إغلاق المحبس اليدوي لخط مياه الطوارئ لتجنب استهلاك وتفريغ المياه دون داعٍ.
                    4. إطفاء الدوائر المساعدة ثم فصل القاطع الرئيسي (Main Isolator OFF).
                    
                    6.4.1 خطوات إغلاق الباب:
                    • تحريك الباب نحو الفرن.
                    • تحويل المفتاح إلى وضع "CLOSE DOOR"، وعند إضاءة المصباح الأبيض:
                    • الضغط المتزامن باليدين على زري ACT1 و ACT2 في ريموت التحكم الأصفر حتى انغلاق حلقات الكبس ومكبس الأمان بالتتابع.
                    • قفل المزلاج اليدوي (Manual Latch) لإحكام مكبس الأمان.
                    
                    6.4.2 خطوات فتح الباب:
                    • التأكد من انتهاء الدورة وانخفاض الحرارة دون 90°م.
                    • فتح المزلاج اليدوي لتصريف أي ضغط متبقٍ.
                    • تحويل المفتاح إلى "OPEN DOOR" والضغط المتزامن على ACT1 + ACT2 لفتح مكبس الأمان وحلقات الباب.
                    • سحب الباب يدوياً حتى يعشق قفل الفتح، والشحن/التفريغ بالرافعة الشوكية.
                """.trimIndent(),
                keyPointsEn = "Startup sequence • Shutdown pump to 0.1 mbar • Close door: ACT1+ACT2 + manual latch • Open door: <90°C",
                keyPointsAr = "تسلسل البدء • إيقاف الفرن بسحب الفراغ لـ 0.1 م.بار • إغلاق الباب: ACT1+ACT2 والمزلاج • فتح الباب: أقل من 90°م",
                category = "OPERATION",
                rawPdfOcrExcerpt = "Startup: emergency water valves open. Shutdown: cold below 100C pump down to 1E-1 mbar. Close door: selector CLOSE DOOR hold ACT1 ACT2 close manual latch. Open door: below 90C open manual latch ACT1 ACT2."
            ),
            ManualDocumentEntity(
                id = "doc_ch8_furnace_maintenance",
                pageNumber = 81,
                chapterNumber = "8",
                chapterTitleEn = "Furnace Maintenance",
                chapterTitleAr = "الصيانة الوقائية وعزوم الربط المقننة",
                sectionNumber = "8.1 - 8.6",
                sectionTitleEn = "LOTO Safety, Daily/Weekly Checks, CFC Torques & Triennial Valve",
                sectionTitleAr = "إجراءات العزل والقفل (LOTO)، الصيانة اليومية والأسبوعية وعزوم CFC",
                contentEn = """
                    8.0 MAINTENANCE SAFETY & LOCKOUT/TAGOUT (LOTO):
                    • Main electrical switch must be switched off and locked with a PADLOCK.
                    • Gas inlet lockable manual valve and pneumatic lockable manual valve must be padlocked.
                    • The 50-liter air buffer tank must be discharged by opening venting valve (PnViMaVa).
                    
                    8.1 DAILY MAINTENANCE (Operator):
                    • Check mechanical pump & diffusion pump oil levels through inspection glass (color and contamination).
                    • Check thermocouples visually for breaks or distortion.
                    • Clean chamber interior and door vacuum seal with vacuum cleaner.
                    • Verify water flow indicators on manifold and manometers (pneumatic 5-6 bar).
                    
                    8.2 WEEKLY MAINTENANCE (Technician):
                    • Check condition of heating chamber and tightening of CFC heating element screws with DYNAMOMETRIC WRENCH:
                      * M14 CFC: 2.5 Nm
                      * M16 CFC: 3.5 Nm
                      * M24 CFC: 8.0 Nm
                    • Inspect alumina supports, molybdenum shields, and earth insulation.
                    • Clean door vacuum seal (O-ring) with soft cloth and alcohol; replace if radial cracks appear.
                    • Perform leak rate check (< 25 micron/h) after burnout cycle.
                    
                    8.3 - 8.5 MONTHLY & YEARLY CHECKS:
                    • Monthly: Foam leak check on inert gas fittings; verify cooling water hardness (<= 15 Fr°).
                    • Six-monthly: Inspect heating contactors, tighten transformer primary/secondary terminal screws.
                    • Yearly: Full instrumentation calibration (EPC3016, 6180A recorder, thermocouples) using precision calibrator.
                    
                    8.6 TRIENNIAL MAINTENANCE:
                    • Safety pressure relief valve maximum service life is 36 MONTHS. Valve must be dismantled and returned to factory for calibration check.
                """.trimIndent(),
                contentAr = """
                    8.0 قواعد الأمان والعزل قبل الصيانة (LOTO):
                    • إطفاء القاطع الكهربائي الرئيسي وقفله بقفل الأمان (Padlock).
                    • إغلاق وقفل المحابس اليدوية لخطوط الغاز والهواء المضغوط.
                    • تفريغ خزان الهواء المضغوط 50 لتر بفتح محبس التنفيس PnViMaVa والتأكد من هبوط الضغط إلى الصفر.
                    
                    8.1 الصيانة اليومية (المشغل):
                    • فحص مستوى ولون ونظافة زيت المضخات الميكانيكية ومضخة الانتشار عبر زجاجة البيان.
                    • فحص بصري للمزدوجات الحرارية والقوابس.
                    • تنظيف غرفة التسخين وجوان الباب بمكنسة شفط صناعية.
                    • مراقبة دوران مؤشرات تدفق مياه التبريد ومقاييس الضغط (النيوماتيك 5-6 بار).
                    
                    8.2 الصيانة الأسبوعية (فني الصيانة):
                    • فحص غرفة التسخين وإعادة ربط مسامير عناصر التسخين الكربونية بمفتاح العزم المقنن:
                      * مسمار M14 CFC: عزم 2.5 نيوتن.متر (Nm).
                      * مسمار M16 CFC: عزم 3.5 نيوتن.متر (Nm).
                      * مسمار M24 CFC: عزم 8.0 نيوتن.متر (Nm).
                    • فحص دعامات السيراميك (الألومينا) وشاشات الموليبدينوم والعزل عن الأرضي.
                    • تنظيف جوان الباب الفراغي (O-ring) بكحول وقماش ناعم، واستبداله فوراً عند وجود تشققات.
                    • قياس معدل التسريب (< 25 ميكرون/ساعة).
                    
                    8.3 - 8.5 الصيانة الشهرية والنصف سنوية والسنوية:
                    • شهرياً: فحص تسريب الغاز بالرغوة واختبار عسورة المياه (<= 15 درجة فرنسية).
                    • كل 6 أشهر: فحص نقاط تلامس كونتكتورات القدرة وشد مسامير المحول الكهربائي Tr1/Tr2/Tr3.
                    • سنوياً: معايرة شاملة لأجهزة التحكم EPC3016 ومسجل 6180A وحساسات الحرارة بجهاز معايرة مرجعي.
                    
                    8.6 الصيانة كل 3 سنوات (36 شهراً):
                    • أقصى عمر تشغيلي لصمام أمان الضغط الزائد هو 36 شهراً، ويجب فك الصمام وإرساله للمصنع لإعادة المعايرة.
                """.trimIndent(),
                keyPointsEn = "LOTO padlock • M14 CFC 2.5Nm • M16 CFC 3.5Nm • M24 CFC 8Nm • Door seal alcohol • Safety valve 36 months",
                keyPointsAr = "إجراءات LOTO • عزم مسامير CFC: M14=2.5Nm / M16=3.5Nm / M24=8Nm • تنظيف الجوان بكحول • صمام الأمان 36 شهراً",
                category = "MAINTENANCE",
                rawPdfOcrExcerpt = "Maintenance: main switch padlocked, 50l air tank emptied. Torques: M14 CFC 2,5 Nm, M16 CFC 3,5 Nm, M24 CFC 8 Nm. Door vacuum seal clean with alcohol. Safety valve triennial 36 months."
            ),
            ManualDocumentEntity(
                id = "doc_ch10_ce_plate",
                pageNumber = 89,
                chapterNumber = "10",
                chapterTitleEn = "CE Plate & Identification",
                chapterTitleAr = "لوحة المطابقة وبيانات المصنع (CE Plate)",
                sectionNumber = "10.0",
                sectionTitleEn = "Official CE Plate & Nameplate Specifications",
                sectionTitleAr = "لوحة المواصفات وبيانات اعتماد CE",
                contentEn = """
                    TAV VACUUM FURNACES S.p.A.
                    Via Dell'Industria 11, 24043 Caravaggio (BG), Italy
                    CE CERTIFICATION PLATE:
                    • Model: TPH 120/120/230
                    • Type: TAV H12
                    • Serial Number: 911
                    • Year of Build: 2023
                    • Fluid Groups: Gr. 2 (N2), Gr. 2 (Ar), Gr. 2 (H2O), Gr. 2 (H2)
                    • Heating Power: 480 kW
                    • Connected Load: 750 kVA
                    • Line Voltage: 400 V, 50 Hz
                    • Max Load: 2000 kg
                    WARNING: The CE plate is placed on the furnace near the door: IT MUST NEVER BE REMOVED.
                """.trimIndent(),
                contentAr = """
                    شركة TAV VACUUM FURNACES S.p.A.
                    العنوان: Caravaggio (BG), Italy
                    لوحة المطابقة الأوروبية الرسمية CE:
                    • الطراز: TPH 120/120/230
                    • النوع: TAV H12
                    • الرقم التسلسلي: 911
                    • سنة الصنع: 2023
                    • مجموعات الموائع: غاز النيتروجين N2، غاز الآرجون Ar، مياه التبريد H2O، والهيدروجين H2.
                    • قدرة التسخين: 480 كيلوواط (kW).
                    • إجمالي الحمل المتصل: 750 ك.ف.أ (kVA).
                    • جهد التغذية: 400 فولت، 50 هرتز.
                    • أقصى وزن للشحنة: 2000 كجم.
                    تحذير: اللوحة مثبتة قرب باب الفرن ويُحظر إزالتها نهائياً.
                """.trimIndent(),
                keyPointsEn = "TAV H12 • S/N 911 • Year 2023 • 480 kW • 750 kVA • 400V 50Hz • 2000 kg",
                keyPointsAr = "TAV H12 • رقم 911 • سنة 2023 • 480 ك.و • 750 ك.ف.أ • 400ف 50هرتز • 2000 كجم",
                category = "CE_PLATE",
                rawPdfOcrExcerpt = "CE PLATE Model TPH 120/120/230 Type TAV H12 Serial 911 Year 2023 Heating 480 kW Connected 750 kVA Line 400V 50Hz Max load 2000kg"
            ),
            ManualDocumentEntity(
                id = "doc_ch8_gas_quench_dynamics",
                pageNumber = 90,
                chapterNumber = "8",
                chapterTitleEn = "Gas Quench Aerodynamics & Heat Exchanger",
                chapterTitleAr = "ديناميكا التبريد السريع بالغاز والمبادل الحراري 160kW",
                sectionNumber = "8.1",
                sectionTitleEn = "High Pressure Gas Quench (HPGQ) Operation at 6-10 bar",
                sectionTitleAr = "تشغيل التبريد بالغاز فائق الضغط (6 إلى 10 بار) ونظام التوجيه 360°",
                contentEn = """
                    HIGH PRESSURE GAS QUENCHING (HPGQ) SYSTEM:
                    • Blower Motor: 160 kW high-speed centrifugal blower (Boldrocchi/TAV)
                    • Circulation Rate: up to 35,000 Nm³/h at 6-10 bar abs
                    • Gas Velocity at Nozzles: 40 - 55 m/s
                    • Heat Exchanger: Finned Cu-Ni tubes, 900 l/min cooling water flow, 480 kW continuous heat rejection
                    • Nozzle Distribution: 360° cylindrical plenum with 4 alternating sectors (Top, Bottom, Lateral Left, Lateral Right)
                    • Control Valves: Fast injection valve (5 sec full open), automated venting valve, 6.5 bar rupture disk
                    • Quench Gas Selection: High-purity Nitrogen (N2 >99.999%) for tool steels (D2, H13); Argon (Ar >99.999%) for superalloys (Inconel 718, Titanium).
                """.trimIndent(),
                contentAr = """
                    منظومة التبريد الغازي فائق الضغط (HPGQ):
                    • محرك مروحة التبريد: 160 كيلوواط طاردة مركزية فائقة السرعة.
                    • معدل تدوير الغاز: يصل إلى 35,000 م³ قياسي/ساعة عند ضغط 6 إلى 10 بار.
                    • سرعة تدفق الغاز عند الفوهات: 40 إلى 55 م/ثانية.
                    • المبادل الحراري: أنابيب مزعنفة من النحاس والنيكل، تدفق مياه 900 لتر/دقيقة، تصريف حراري 480 كيلوواط.
                    • توزيع الفوهات: موزع أسطواني 360 درجة بـ 4 قطاعات تبادلية (علوي، سفلي، يمين، يسار) لمنع تشوه القوالب.
                    • صمامات التحكم: صمام حقن سريع (يفتح بالكامل في 5 ثوانٍ)، صمام تنفيس آلي، وقرص تمزق أمان 6.5 بار.
                    • نوع الغاز المستخدم: نيتروجين عالي النقاوة (>99.999%) لصلب القوالب؛ آرجون (>99.999%) للسبائك الفائقة كالتيتانيوم والإنكونيل.
                """.trimIndent(),
                keyPointsEn = "160 kW Blower • 35,000 m³/h • 6-10 bar N2/Ar • 360° Nozzles • Cu-Ni 900 l/min Exchanger • 6.5 bar Relief",
                keyPointsAr = "مروحة 160 ك.و • تدفق 35000 م³/ساعة • ضغط 6-10 بار • فوهات 360° • مبادل 900 ل/د • صمام أمان 6.5 بار",
                category = "OPERATION",
                rawPdfOcrExcerpt = "HPGQ: 160 kW blower, 35000 m3/h, 6-10 bar N2/Ar, Cu-Ni heat exchanger 900 l/min, 360-degree aerodynamic nozzles."
            ),
            ManualDocumentEntity(
                id = "doc_ch9_pyrometry_ams2750g",
                pageNumber = 91,
                chapterNumber = "9",
                chapterTitleEn = "Aerospace Pyrometry & Nadcap Compliance",
                chapterTitleAr = "المعايير البيرومترية الفضائية واعتماد Nadcap (AMS 2750G)",
                sectionNumber = "9.1",
                sectionTitleEn = "AMS 2750G Pyrometry Standards, SAT & TUS Testing",
                sectionTitleAr = "معايير القياس الحراري AMS 2750G واختبارات SAT و TUS وتتبع عمر المجسات",
                contentEn = """
                    PYROMETRY COMPLIANCE (AMS 2750G / NADCAP AC7102):
                    • Furnace Pyrometry Class: Class 2 (±5.0 °C / ±10.0 °F uniformity across 500°C - 1280°C range)
                    • Instrumentation Type: Type B (Closed loop control + Multi-channel record + Independent hardware over-temp)
                    • Temperature Uniformity Survey (TUS): 9-point thermocouple survey in 1200x1200x2300 mm workspace
                    • System Accuracy Test (SAT): Bi-weekly or monthly test, allowable deviation ΔT ≤ 1.7 °C (±3.0 °F)
                    • Thermocouple Exposure Limits:
                      - Type K (Chromel-Alumel): Maximum 90 uses or 3 months (whichever occurs first)
                      - Type N (Nicrosil-Nisil): Maximum 180 uses or 6 months
                      - Type S (Pt/Pt-10%Rh Control): Annual primary calibration standard replacement
                    • Eurotherm EPC3016 PID controller calibrated annually to traceable national standards.
                """.trimIndent(),
                contentAr = """
                    مطابقة معايير البيرومتري الفضائي (AMS 2750G / NADCAP AC7102):
                    • فئة التجانس الحراري: Class 2 (تجانس ±5.0°م / ±10.0°ف عبر مدى التشغيل 500°م إلى 1280°م).
                    • نوع الأجهزة المعتمد: Type B (تحكم مغلق + تسجيل متعدد القنوات + قاطع حراري ميكانيكي مستقل).
                    • مسح التماثل الحراري (TUS): مسح دوري بـ 9 نقاط مجسات داخل حيز التشغيل 1200×1200×2300 مم.
                    • اختبار دقة المنظومة (SAT): اختبار دوري، أقصى حيود مسموح به هو ΔT ≤ 1.7°م.
                    • حدود استخدام المجسات الحرارية للشحنة:
                      - النوع K: أقصى حد 90 استخداماً أو 3 أشهر (أيهما أقرب).
                      - النوع N: أقصى حد 180 استخداماً أو 6 أشهر.
                      - النوع S البلاتيني: إعادة معايرة سنوية معتمدة.
                    • معايرة سنوية لوحدة Eurotherm EPC3016 وفق المعايير القومية المعتمدة.
                """.trimIndent(),
                keyPointsEn = "AMS 2750G Class 2 (±5°C) • Type B Instruments • 9-Point TUS • SAT ΔT ≤ 1.7°C • Type K max 90 uses • Type N max 180 uses",
                keyPointsAr = "AMS 2750G Class 2 (±5°م) • أجهزة Type B • مسح TUS 9 نقاط • اختبار SAT ΔT ≤ 1.7°م • مجسات K أقصى حد 90 مرة",
                category = "PYROMETRY",
                rawPdfOcrExcerpt = "Pyrometry AMS 2750G Class 2 +/- 5C, Type B instrumentation, SAT limit 1.7C, TUS 9 points, thermocouple limits K 90 uses, N 180 uses."
            ),
            ManualDocumentEntity(
                id = "doc_ch11_poka_yoke_loading",
                pageNumber = 92,
                chapterNumber = "11",
                chapterTitleEn = "Poka-Yoke Loading & Hot Zone Protection",
                chapterTitleAr = "قواعد التحميل ومنع الخطأ البشري (Poka-Yoke) وحماية القفص الحراري",
                sectionNumber = "11.1",
                sectionTitleEn = "Poka-Yoke Checklist for Operators & Charge Preparation",
                sectionTitleAr = "قائمة التحقق المانعة للخطأ وتأمين سلامة سلات الكربون وعناصر التسخين",
                contentEn = """
                    POKA-YOKE LOADING GUIDELINES & SAFE OPERATION:
                    1. Weight Limit Verification:
                       - Nominal working load: 1200 kg
                       - Absolute maximum gross load: 2000 kg (including charge baskets and fixtures)
                       - Mandatory verification on calibrated workshop scale before loading.
                    2. Hot Zone Clearance (>50 mm):
                       - Maintain minimum 50 mm clearance between outermost parts and graphite heating elements.
                       - Avoid electrical arcing or short-circuit between charge and 480 kW heating elements.
                    3. Load Thermocouple Attachment:
                       - Place trailing thermocouples at geometric core and corner surfaces.
                       - Secure firmly with pure Inconel/Nichrome wire to prevent detachment during gas blast.
                    4. Fixture Integrity (CFC / Molybdenum):
                       - Inspect carbon composite fixtures for cracked grid bars or loose molybdenum stop-pins.
                       - Tighten CFC screws to specified torques: M14=2.5 Nm, M16=3.5 Nm, M24=8.0 Nm.
                    5. Degreasing & Cleanliness:
                       - All components must be degreased and 100% dry before loading to prevent oil outgassing and vacuum pump contamination.
                """.trimIndent(),
                contentAr = """
                    إرشادات التحقق المانعة للخطأ (Poka-Yoke) وسلامة التشغيل:
                    1. التحقق الإلزامي من الوزن:
                       - الحمولة التشغيلية القياسية: 1200 كجم.
                       - أقصى حمولة إجمالية مطلقة: 2000 كجم (تشمل وزن القطع والسلات والدعامات).
                       - فحص إلزامي للوزن على ميزان الورشة قبل الإدخال.
                    2. خلوص سخانات الجرافيت (>50 مم):
                       - الالتزام بخلوص أمان لا يقل عن 50 مم بين أطراف القطع وعناصر التسخين الجرافيتية.
                       - منع حدوث أي قوس كهربائي أو قصر في دائرة الـ 480 كيلوواط.
                    3. تثبيت مجسات الشحنة الحرارية:
                       - غرس المجسات في قلب القطع وعلى الأطراف الحساسة.
                       - ربط المجسات بإحكام بسلك الإنكونيل النقي لمنع انفصالها بفعل سرعة الغاز النفاث.
                       - فحص سلامة سلات الكربون CFC والتأكد من عزم ربط المسامير: M14=2.5Nm / M16=3.5Nm / M24=8Nm.
                       - تنظيف وإزالة الزيوت عن كافة القطع وتجفيفها بالكامل قبل التحميل لمنع تلويث زيت مضخات الفراغ.
                """.trimIndent(),
                keyPointsEn = "Max gross load 2000 kg • Graphite clearance >50 mm • Core T/C attachment • CFC torque M14 2.5Nm • Mandatory degreasing",
                keyPointsAr = "أقصى حمولة 2000 كجم • خلوص الجرافيت >50 مم • تثبيت مجس القلب بسلك إنكونيل • عزم CFC M14=2.5Nm • إزالة الشحوم",
                category = "OPERATION",
                rawPdfOcrExcerpt = "Poka-Yoke: max load 2000 kg, clearance > 50 mm from graphite elements, core thermocouple attached, degreasing required."
            )
        )
    }
}
