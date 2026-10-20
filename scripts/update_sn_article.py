"""
Generate sn-article-updated.pdf with tables and architecture claims
aligned to the SmartPest Android codebase (not illustrative placeholders).
"""

from pathlib import Path

from reportlab.lib import colors
from reportlab.lib.enums import TA_CENTER, TA_JUSTIFY, TA_LEFT
from reportlab.lib.pagesizes import A4
from reportlab.lib.styles import ParagraphStyle, getSampleStyleSheet
from reportlab.lib.units import cm, mm
from reportlab.platypus import (
    HRFlowable,
    Paragraph,
    SimpleDocTemplate,
    Spacer,
    Table,
    TableStyle,
)

OUT = Path(r"C:\Users\Raghav\Downloads\sn-article-updated.pdf")
# Also overwrite a project-local copy for convenience
OUT_PROJECT = Path(r"C:\Users\Raghav\AndroidStudioProjects\SmartPest\docs\sn-article-updated.pdf")

page_w, page_h = A4


def build_styles():
    styles = getSampleStyleSheet()
    styles.add(
        ParagraphStyle(
            name="Authors",
            parent=styles["Normal"],
            fontName="Times-Roman",
            fontSize=11,
            leading=14,
            alignment=TA_CENTER,
            spaceAfter=6,
        )
    )
    styles.add(
        ParagraphStyle(
            name="Affil",
            parent=styles["Normal"],
            fontName="Times-Italic",
            fontSize=9,
            leading=12,
            alignment=TA_CENTER,
            spaceAfter=2,
            textColor=colors.HexColor("#333333"),
        )
    )
    styles.add(
        ParagraphStyle(
            name="PaperTitle",
            parent=styles["Normal"],
            fontName="Times-Bold",
            fontSize=14,
            leading=18,
            alignment=TA_CENTER,
            spaceBefore=10,
            spaceAfter=12,
        )
    )
    styles.add(
        ParagraphStyle(
            name="AbsHead",
            parent=styles["Normal"],
            fontName="Times-Bold",
            fontSize=10,
            leading=12,
            spaceBefore=8,
            spaceAfter=4,
        )
    )
    styles.add(
        ParagraphStyle(
            name="BodyJust",
            parent=styles["Normal"],
            fontName="Times-Roman",
            fontSize=10,
            leading=13,
            alignment=TA_JUSTIFY,
            spaceAfter=8,
            firstLineIndent=12,
        )
    )
    styles.add(
        ParagraphStyle(
            name="BodyJustFirst",
            parent=styles["Normal"],
            fontName="Times-Roman",
            fontSize=10,
            leading=13,
            alignment=TA_JUSTIFY,
            spaceAfter=8,
            firstLineIndent=0,
        )
    )
    styles.add(
        ParagraphStyle(
            name="SecHead",
            parent=styles["Normal"],
            fontName="Times-Bold",
            fontSize=12,
            leading=15,
            spaceBefore=14,
            spaceAfter=6,
        )
    )
    styles.add(
        ParagraphStyle(
            name="SubHead",
            parent=styles["Normal"],
            fontName="Times-Bold",
            fontSize=10.5,
            leading=13,
            spaceBefore=10,
            spaceAfter=4,
        )
    )
    styles.add(
        ParagraphStyle(
            name="Kw",
            parent=styles["Normal"],
            fontName="Times-Roman",
            fontSize=9.5,
            leading=12,
            spaceBefore=4,
            spaceAfter=8,
        )
    )
    styles.add(
        ParagraphStyle(
            name="Cap",
            parent=styles["Normal"],
            fontName="Times-Roman",
            fontSize=9,
            leading=11,
            alignment=TA_LEFT,
            spaceBefore=4,
            spaceAfter=10,
        )
    )
    styles.add(
        ParagraphStyle(
            name="Cell",
            parent=styles["Normal"],
            fontName="Times-Roman",
            fontSize=8.5,
            leading=10.5,
        )
    )
    styles.add(
        ParagraphStyle(
            name="CellB",
            parent=styles["Normal"],
            fontName="Times-Bold",
            fontSize=8.5,
            leading=10.5,
        )
    )
    styles.add(
        ParagraphStyle(
            name="Alg",
            parent=styles["Normal"],
            fontName="Courier",
            fontSize=8,
            leading=10.5,
        )
    )
    styles.add(
        ParagraphStyle(
            name="Ref",
            parent=styles["Normal"],
            fontName="Times-Roman",
            fontSize=9,
            leading=11.5,
            leftIndent=14,
            firstLineIndent=-14,
            spaceAfter=4,
        )
    )
    styles.add(
        ParagraphStyle(
            name="BulletBody",
            parent=styles["Normal"],
            fontName="Times-Roman",
            fontSize=10,
            leading=13,
            alignment=TA_JUSTIFY,
        )
    )
    styles.add(
        ParagraphStyle(
            name="FooterNote",
            parent=styles["Normal"],
            fontName="Times-Italic",
            fontSize=8,
            leading=10,
            textColor=colors.HexColor("#444444"),
        )
    )
    return styles


def make_table(data, col_widths, header=True):
    t = Table(data, colWidths=col_widths, repeatRows=1 if header else 0)
    style_cmds = [
        ("FONTNAME", (0, 0), (-1, -1), "Times-Roman"),
        ("FONTSIZE", (0, 0), (-1, -1), 8.5),
        ("VALIGN", (0, 0), (-1, -1), "TOP"),
        ("LEFTPADDING", (0, 0), (-1, -1), 4),
        ("RIGHTPADDING", (0, 0), (-1, -1), 4),
        ("TOPPADDING", (0, 0), (-1, -1), 3),
        ("BOTTOMPADDING", (0, 0), (-1, -1), 3),
        ("GRID", (0, 0), (-1, -1), 0.4, colors.HexColor("#666666")),
        ("BOX", (0, 0), (-1, -1), 0.8, colors.HexColor("#333333")),
    ]
    if header:
        style_cmds += [
            ("BACKGROUND", (0, 0), (-1, 0), colors.HexColor("#E8E8E8")),
            ("FONTNAME", (0, 0), (-1, 0), "Times-Bold"),
        ]
    if len(data) > 2 and "Total" in str(data[-1][0]):
        style_cmds.append(("BACKGROUND", (0, -1), (-1, -1), colors.HexColor("#F0F0F0")))
        style_cmds.append(("FONTNAME", (0, -1), (-1, -1), "Times-Bold"))
    t.setStyle(TableStyle(style_cmds))
    return t


def build_story(styles):
    def P(text, style="BodyJust"):
        return Paragraph(text, styles[style])

    def cell(text, bold=False):
        return Paragraph(text, styles["CellB" if bold else "Cell"])

    story = []

    story.append(P("SmartPest Project Authors", "Authors"))
    story.append(
        P(
            "<b>SmartPest: An AI-Powered Mobile System for Crop Disease Detection, "
            "Advisory Support, and Precision Farming Assistance</b>",
            "PaperTitle",
        )
    )
    story.append(
        P(
            "<sup>1</sup>Department of Computer Science and Engineering<br/>"
            "Based on the implemented SmartPest Android application codebase "
            "(Kotlin, Jetpack Compose, TensorFlow Lite, Firebase, Gemini API).",
            "Affil",
        )
    )
    story.append(Spacer(1, 6))
    story.append(HRFlowable(width="100%", thickness=0.6, color=colors.black, spaceAfter=8))

    story.append(P("Abstract", "AbsHead"))
    story.append(
        P(
            "Timely detection of crop diseases and access to reliable agronomic advice remain "
            "persistent challenges for smallholder farmers, particularly in regions with limited "
            "access to agricultural extension services. This paper presents <b>SmartPest</b>, "
            "an AI-powered Android mobile system that combines <b>on-device TensorFlow Lite</b> "
            "deep learning for crop disease detection, a <b>Gemini 1.5 Flash</b>–powered conversational "
            "advisory assistant, a curated farm-guide knowledge base, real-time weather reporting "
            "via a public weather API, and Firebase-backed authentication and local alert delivery. "
            "The deployed classifier uses a packaged <tt>1.tflite</tt> model (12.35&nbsp;MB) that maps "
            "224×224 leaf images to one of <b>38 disease/healthy classes</b> spanning 14 crops "
            "(PlantVillage-style taxonomy as encoded in the application). Predictions with maximum "
            "class probability below a confidence threshold <i>τ</i>&nbsp;=&nbsp;0.7 are flagged as uncertain. "
            "We describe the system architecture as implemented in the codebase, the disease-class "
            "inventory, model and runtime configuration, and the mobile application workflow "
            "(onboarding, authentication, home dashboard, disease scan, AI assistant, weather, "
            "alerts, farm guide, and profile). We discuss limitations relative to idealized hybrid "
            "cloud/IoT designs and outline directions for future work including offline advisory "
            "caching, expanded treatment mapping per disease class, and field evaluation.",
            "BodyJustFirst",
        )
    )
    story.append(
        P(
            "<b>Keywords:</b> agriculture · crop disease detection · TensorFlow Lite · mobile application · "
            "deep learning · Gemini · Firebase · precision farming · Jetpack Compose",
            "Kw",
        )
    )

    story.append(P("1 Introduction", "SecHead"))
    story.append(
        P(
            "Agriculture continues to be the primary source of livelihood for a large share of the "
            "world’s population, and crop losses due to pests and diseases remain one of the most "
            "significant threats to food security, particularly among smallholder farmers who often "
            "lack timely access to agricultural extension officers or plant-pathology expertise "
            "(Food and Agriculture Organization 2021). Early and accurate diagnosis of crop disease "
            "is critical: delayed identification can lead to inappropriate or excessive pesticide use, "
            "reduced yield, and increased production cost.",
            "BodyJustFirst",
        )
    )
    story.append(
        P(
            "The widespread availability of low-cost smartphones and improvements in on-device machine "
            "learning have created an opportunity to bring disease diagnosis capability directly into "
            "farmers’ hands. Convolutional neural networks (CNNs) have demonstrated strong performance "
            "on image-based plant disease classification tasks (Mohanty et al. 2016; Ferentinos 2018; "
            "Kumar et al. 2021), and TensorFlow Lite now makes it practical to ship compact models "
            "inside Android applications for offline inference.",
            "BodyJust",
        )
    )
    story.append(
        P(
            "This paper presents <b>SmartPest</b> as implemented in the project codebase: a single "
            "farmer-facing Android application that integrates (i) on-device disease classification from "
            "leaf images, (ii) LLM-based advisory chat (Gemini 1.5 Flash) plus a structured farm guide, "
            "and (iii) weather- and notification-driven assistance for day-to-day farm decisions. "
            "Our contributions, grounded in the actual source code, are as follows:",
            "BodyJust",
        )
    )
    bullets = [
        "We document the end-to-end architecture of the SmartPest Android client: Jetpack Compose UI, Room persistence, Firebase Auth/FCM, Retrofit weather client, TensorFlow Lite inference, and Gemini chat.",
        "We enumerate the 38-class disease taxonomy used by <tt>TensorFlowHelper</tt>, the 224×224 preprocessing pipeline, and the confidence threshold <i>τ</i> = 0.7 that gates uncertain results.",
        "We report deployment-relevant model statistics taken from the packaged asset (<tt>1.tflite</tt>, 12.35 MB) and the application configuration (minSdk 28, targetSdk 34, ML model binding).",
        "We describe the farmer-facing modules actually shipped: PestDisease.AI scan, AI Assistant, Weather Report, Local Alerts, Farm Guide (19 problem–solution items), and Profile.",
    ]
    for b in bullets:
        story.append(Paragraph("• " + b, styles["BulletBody"]))
        story.append(Spacer(1, 3))

    story.append(
        P(
            "The remainder of this paper is organized as follows. Section 2 reviews related work. "
            "Section 3 describes the SmartPest system architecture as implemented. Section 4 details "
            "implementation data derived from the codebase. Section 5 discusses evaluation posture and "
            "deployment metrics. Section 6 discusses limitations, and Section 7 concludes.",
            "BodyJust",
        )
    )

    story.append(P("2 Related Work", "SecHead"))
    story.append(P("2.1 Image-based crop disease detection", "SubHead"))
    story.append(
        P(
            "Deep CNNs substantially improved plant disease classification accuracy. Mohanty et al. (2016) "
            "showed that CNNs trained on a large public leaf-image dataset (PlantVillage) could classify "
            "multiple crop–disease pairs with high accuracy under controlled imaging conditions. "
            "Ferentinos (2018) extended this to a broader set of crops and disease categories. "
            "The SmartPest class list in code matches the well-known PlantVillage-style 38-class "
            "taxonomy (including healthy classes), which is the label space consumed by the on-device model.",
            "BodyJustFirst",
        )
    )
    story.append(P("2.2 On-device and mobile inference", "SubHead"))
    story.append(
        P(
            "Deploying deep learning models on smartphones requires balancing accuracy against model size "
            "and latency. Chen et al. (2020) evaluated lightweight CNN architectures for on-device crop "
            "disease recognition. SmartPest ships a single packaged TensorFlow Lite model "
            "(<tt>app/src/main/ml/1.tflite</tt>) loaded via Android ML Model Binding "
            "(<tt>AutoModel1</tt>), rather than a dual on-device/cloud pair. All inference therefore "
            "runs locally on the handset.",
            "BodyJustFirst",
        )
    )
    story.append(P("2.3 Agricultural advisory and precision farming systems", "SubHead"))
    story.append(
        P(
            "Beyond detection, Ramesh et al. (2023) highlighted multilingual chat-style advisory for "
            "farmers. SmartPest implements conversational advice through Google’s Generative AI SDK "
            "(model <tt>gemini-1.5-flash</tt>) and complements it with a static Farm Guide of common "
            "agronomic problems. Weather context is obtained from WeatherAPI.com through Retrofit; "
            "push-style local alerts are delivered with Firebase Cloud Messaging and stored in Room.",
            "BodyJustFirst",
        )
    )

    story.append(P("3 System Architecture", "SecHead"))
    story.append(P("3.1 Overview", "SubHead"))
    story.append(
        P(
            "As implemented, SmartPest is composed of four principal components: (1) an Android mobile "
            "client (Kotlin + Jetpack Compose), (2) an on-device disease-detection inference layer "
            "(TensorFlow Lite), (3) an advisory layer (Gemini chat + Farm Guide), and (4) a precision-assist "
            "layer (weather API + Firebase alerts + Room). Table&nbsp;1 summarizes the mapping from "
            "logical components to source modules.",
            "BodyJustFirst",
        )
    )

    arch_data = [
        [cell("Component", True), cell("Implementation in codebase", True)],
        [cell("Mobile client"), cell("Kotlin, Jetpack Compose, Navigation, Material 3; screens in <tt>view/*</tt>")],
        [cell("Auth"), cell("Firebase Authentication (<tt>AuthViewModel</tt>); login/signup flow")],
        [cell("Disease scan"), cell("<tt>PestDiseaseAI</tt> + <tt>TensorFlowHelper.ClassifyPestOrDisease</tt>")],
        [cell("On-device model"), cell("<tt>1.tflite</tt> via <tt>AutoModel1</tt> (ML model binding)")],
        [cell("AI advisory"), cell("Gemini 1.5 Flash (<tt>ChatViewModel</tt> / GenerativeModel)")],
        [cell("Farm guide"), cell("19 curated problem–solution pairs (<tt>FarmGuide</tt>)")],
        [cell("Weather"), cell("WeatherAPI.com via Retrofit (<tt>WeatherViewModel</tt>)")],
        [cell("Alerts"), cell("FCM (<tt>MyFirebaseMessagingService</tt>) + Room <tt>Alert</tt> entity")],
        [cell("Local storage"), cell("Room DB: <tt>User</tt>, <tt>Alert</tt> (<tt>AppDatabase</tt>)")],
        [cell("Profile"), cell("Name, location, phone, language; drives weather city lookup")],
    ]
    story.append(make_table(arch_data, [3.2 * cm, 13.0 * cm], header=True))
    story.append(
        P(
            "<b>Table 1</b> SmartPest system components as implemented in the project source tree "
            "(package <tt>com.example.smartpest</tt>).",
            "Cap",
        )
    )

    story.append(P("3.2 Mobile application module", "SubHead"))
    story.append(
        P(
            "The mobile client targets Android API 28+ (<tt>minSdk = 28</tt>, <tt>targetSdk = 34</tt>, "
            "<tt>compileSdk = 34</tt>) with application id <tt>com.example.smartpest</tt>. Primary "
            "navigation routes include Home, PestDisease.AI, AI Assistant, Weather Report, Local Alerts, "
            "Farm Guide, and Profile Page, plus onboarding, login, and signup. The home dashboard greets "
            "the user by time of day, shows weather for the profile location, recent alerts, and a "
            "shortcut to disease scanning. First-launch onboarding introduces disease scan, weather, "
            "AI assistant, and local alerts via Lottie animations.",
            "BodyJustFirst",
        )
    )

    story.append(P("3.3 Disease detection model", "SubHead"))
    story.append(
        P(
            "Disease detection is performed entirely on-device. Given a gallery-selected leaf image, the "
            "UI scales the bitmap to 224×224 and invokes <tt>TensorFlowHelper</tt>. Pixels are normalized "
            "to [0,&nbsp;1] as FLOAT32 RGB and written into a tensor of shape <tt>[1, 224, 224, 3]</tt>. "
            "The model returns a probability vector over <i>C</i>&nbsp;=&nbsp;38 classes. The predicted class "
            "ŷ is",
            "BodyJustFirst",
        )
    )
    story.append(P("ŷ = arg&nbsp;max<sub>c ∈ {1,…,C}</sub> p(c | x),", "Authors"))
    story.append(
        P(
            "where p(c | x) is the model output for input image x. If max<sub>c</sub> p(c | x) &lt; "
            "<i>τ</i> with <b><i>τ</i> = 0.7</b> (hard-coded in <tt>TensorFlowHelper</tt>), the application "
            "returns “Uncertain classification” instead of a disease label. There is no cloud-hosted "
            "classifier path in the current codebase.",
            "BodyJustFirst",
        )
    )

    story.append(P("3.4 Advisory and recommendation engine", "SubHead"))
    story.append(
        P(
            "Advisory support is provided in two complementary ways. First, the <b>AI Assistant</b> "
            "(<tt>ExpertSupport</tt> / <tt>ChatViewModel</tt>) opens a multi-turn chat session with "
            "Gemini 1.5 Flash, allowing free-form questions about crops, equipment, and treatments. "
            "Second, the <b>Farm Guide</b> presents 19 expandable problem–solution cards covering "
            "topics such as pest infestation, soil erosion, water scarcity, crop diseases, climate "
            "adaptation, and post-harvest losses. Disease-class labels from the TFLite model are shown "
            "directly in the scan result card; structured per-class chemical/organic treatment records "
            "are not yet wired as a separate offline knowledge base in code.",
            "BodyJustFirst",
        )
    )

    story.append(P("3.5 Precision farming assistance module", "SubHead"))
    story.append(
        P(
            "Precision assistance in the current build focuses on <b>weather</b> and <b>alerts</b>, not "
            "field IoT soil sensors. The user profile stores location (city/region string); "
            "<tt>WeatherViewModel</tt> queries WeatherAPI.com and surfaces temperature, condition, "
            "precipitation, and gust data on Home and Weather Report screens. Local Alerts are ingested "
            "via Firebase Cloud Messaging, persisted with Room (<tt>title</tt>, <tt>message</tt>, "
            "<tt>timestamp</tt>), and listed in the Local Alerts screen. No soil-moisture gateway or "
            "crop-stage calendar scheduler is present in the repository.",
            "BodyJustFirst",
        )
    )

    story.append(P("Algorithm 1 SmartPest end-to-end pipeline (as implemented)", "SubHead"))
    algo_lines = [
        "Require: Leaf image x (gallery), user profile F, threshold τ = 0.7",
        "Ensure: Classification label L and optional advisory context",
        "1:  x224 ← Resize(x, 224, 224); normalize RGB / 255 → FLOAT32 tensor",
        "2:  p(· | x) ← AutoModel1.process(x224)          ▷ on-device TFLite",
        "3:  ŷ ← arg max_c p(c | x);  conf ← max_c p(c | x)",
        "4:  if conf < τ then",
        '5:      L ← "Uncertain classification"',
        "6:  else",
        "7:      L ← classes[ŷ]                            ▷ 38-class label string",
        "8:  end if",
        "9:  Weather ← WeatherAPI(F.location)              ▷ if profile complete",
        "10: Advisory ← GeminiChat(user query) or FarmGuide lookup",
        "11: Alerts ← Room/FCM inbox",
        "12: return L, Weather, Advisory, Alerts",
    ]
    for line in algo_lines:
        story.append(Paragraph(line.replace(" ", "&nbsp;"), styles["Alg"]))
    story.append(Spacer(1, 8))

    story.append(P("4 Implementation", "SecHead"))
    story.append(P("4.1 Disease-class inventory (from code)", "SubHead"))
    story.append(
        P(
            "The label array in <tt>TensorFlowHelper.kt</tt> defines the model’s output space. "
            "Table&nbsp;2 replaces the article’s earlier illustrative Tomato/Rice/Maize/Cotton composition "
            "with the <b>actual 38 classes across 14 crops</b> encoded in the application. Image-count "
            "columns from the prior draft were placeholders and are omitted here because training-set "
            "cardinalities are not checked into the repository; the taxonomy itself is authoritative "
            "for the deployed model.",
            "BodyJustFirst",
        )
    )

    class_rows = [
        [cell("Crop", True), cell("Classes (incl. healthy)", True), cell("Labels in TensorFlowHelper", True)],
        [cell("Apple"), cell("4"), cell("Scab; Black Rot; Cedar Apple Rust; Healthy")],
        [cell("Blueberry"), cell("1"), cell("Healthy")],
        [cell("Cherry"), cell("2"), cell("Powdery Mildew; Healthy")],
        [
            cell("Corn (maize)"),
            cell("4"),
            cell("Cercospora / Gray Leaf Spot; Common Rust; Northern Leaf Blight; Healthy"),
        ],
        [
            cell("Grape"),
            cell("4"),
            cell("Black Rot; Esca (Black Measles); Leaf Blight (Isariopsis); Healthy"),
        ],
        [cell("Orange"), cell("1"), cell("Haunglongbing (Citrus Greening)")],
        [cell("Peach"), cell("2"), cell("Bacterial Spot; Healthy")],
        [cell("Pepper bell"), cell("2"), cell("Bacterial Spot; Healthy")],
        [cell("Potato"), cell("3"), cell("Early Blight; Late Blight; Healthy")],
        [cell("Raspberry"), cell("1"), cell("Healthy")],
        [cell("Soybean"), cell("1"), cell("Healthy")],
        [cell("Squash"), cell("1"), cell("Powdery Mildew")],
        [cell("Strawberry"), cell("2"), cell("Leaf Scorch; Healthy")],
        [
            cell("Tomato"),
            cell("10"),
            cell(
                "Bacterial Spot; Early Blight; Late Blight; Leaf Mold; Septoria Leaf Spot; "
                "Spider Mites; Target Spot; Yellow Leaf Curl Virus; Mosaic Virus; Healthy"
            ),
        ],
        [cell("Total"), cell("38"), cell("PlantVillage-style multi-crop taxonomy")],
    ]
    story.append(make_table(class_rows, [3.0 * cm, 2.4 * cm, 10.8 * cm], header=True))
    story.append(
        P(
            "<b>Table 2</b> Disease/healthy class inventory implemented in "
            "<tt>TensorFlowHelper.ClassifyPestOrDisease</tt> (source of truth for model outputs).",
            "Cap",
        )
    )

    story.append(P("4.2 Model packaging and runtime configuration", "SubHead"))
    story.append(
        P(
            "The trained network is packaged as a TensorFlow Lite flatbuffer and consumed through "
            "Android ML Model Binding. Table&nbsp;3 lists configuration values measured or read directly "
            "from project files—not illustrative placeholders.",
            "BodyJustFirst",
        )
    )

    model_rows = [
        [cell("Parameter", True), cell("Value (from project)", True), cell("Source", True)],
        [cell("Model asset"), cell("1.tflite"), cell("app/src/main/ml/1.tflite")],
        [cell("Model size"), cell("12.35 MB (12,950,792 bytes)"), cell("File length on disk")],
        [cell("Binding class"), cell("com.example.smartpest.ml.AutoModel1"), cell("Generated ML source")],
        [cell("Input shape"), cell("1 × 224 × 224 × 3"), cell("TensorFlowHelper / TensorBuffer")],
        [cell("Input type"), cell("FLOAT32, RGB scaled by 1/255"), cell("TensorFlowHelper")],
        [cell("Output classes C"), cell("38"), cell("classes[] array length")],
        [cell("Confidence threshold τ"), cell("0.7"), cell("if (maxConfidence > 0.7)")],
        [cell("Uncertain handling"), cell('Return "Uncertain classification"'), cell("TensorFlowHelper")],
        [cell("Inference locus"), cell("On-device only"), cell("No cloud CNN API in repo")],
        [cell("mlModelBinding"), cell("enabled"), cell("app/build.gradle.kts")],
        [cell("TFLite support libs"), cell("tensorflow-lite-support 0.1.0"), cell("dependencies")],
    ]
    story.append(make_table(model_rows, [4.0 * cm, 6.2 * cm, 6.0 * cm], header=True))
    story.append(
        P(
            "<b>Table 3</b> On-device model and runtime configuration derived from the SmartPest codebase "
            "and packaged TFLite asset (replaces prior placeholder size/latency table).",
            "Cap",
        )
    )

    story.append(P("4.3 Application stack and modules", "SubHead"))
    stack_rows = [
        [cell("Layer", True), cell("Technology", True), cell("Notes", True)],
        [cell("Language / UI"), cell("Kotlin, Jetpack Compose, Material 3"), cell("Compose compiler 1.5.1")],
        [cell("Min / target SDK"), cell("28 / 34"), cell("Low-cost modern Android devices")],
        [cell("Auth"), cell("Firebase Auth"), cell("Email login & signup")],
        [cell("Messaging"), cell("Firebase Cloud Messaging"), cell("Local alerts channel")],
        [cell("Local DB"), cell("Room 2.6.x + KSP"), cell("User + Alert entities")],
        [cell("Networking"), cell("Retrofit + Gson"), cell("WeatherAPI base URL")],
        [cell("LLM advisory"), cell("generativeai / gemini-1.5-flash"), cell("API key via BuildConfig")],
        [cell("Images"), cell("Coil; gallery picker"), cell("PestDisease.AI flow")],
        [cell("Onboarding"), cell("Accompanist Pager + Lottie"), cell("Four intro pages")],
    ]
    story.append(make_table(stack_rows, [3.2 * cm, 6.5 * cm, 6.5 * cm], header=True))
    story.append(
        P(
            "<b>Table 4</b> Software stack used by the SmartPest Android application "
            "(from <tt>app/build.gradle.kts</tt> and source modules).",
            "Cap",
        )
    )

    story.append(
        P(
            "User profile fields persisted in Room are <tt>name</tt>, <tt>location</tt>, <tt>phone</tt>, "
            "and <tt>language</tt>. Alerts store <tt>title</tt>, <tt>message</tt>, and <tt>timestamp</tt>. "
            "Secrets (<tt>GEMINI_API_KEY</tt>, <tt>WEATHER_API_KEY</tt>) are injected from "
            "<tt>local.properties</tt> into <tt>BuildConfig</tt> and are not committed as source.",
            "BodyJust",
        )
    )

    farm_guide_topics = (
        "Pest Infestation; Soil Erosion; Water Scarcity; Weed Control; Nutrient Deficiency; "
        "Crop Diseases; Climate Change; Low Yield; Market Fluctuations; Post-Harvest Losses; "
        "Salinity in Soil; Flooding and Water logging; Soil Compaction; Crop Frost Damage; "
        "Heat Stress on Crops; Overuse of Fertilizers; Seed Germination Failure; "
        "Inconsistent Rainfall; Invasive Species"
    )
    story.append(
        P(
            f"The Farm Guide currently covers the following <b>19</b> topics: {farm_guide_topics}.",
            "BodyJust",
        )
    )

    story.append(P("5 Results and Evaluation", "SecHead"))
    story.append(P("5.1 What can be reported from the codebase", "SubHead"))
    story.append(
        P(
            "The prior draft of this article included illustrative accuracy, precision, recall, F1, and "
            "latency figures, and a sample confusion matrix over a tomato-only subset. Those values were "
            "explicitly marked as placeholders. <b>No training notebooks, held-out metrics, or benchmark "
            "logs are present in the SmartPest repository</b>, so inventing replacement accuracy numbers "
            "would be scientifically inappropriate. Instead, Table&nbsp;5 summarizes evaluation-relevant "
            "facts that are verifiable from the implementation.",
            "BodyJustFirst",
        )
    )

    eval_rows = [
        [cell("Item", True), cell("Status in project", True)],
        [cell("Output space"), cell("38 multi-crop classes (Table 2)")],
        [cell("Decision rule"), cell("Argmax over output vector; reject if conf ≤ 0.7")],
        [cell("Model footprint"), cell("12.35 MB on-device TFLite; suitable for mid-range phones")],
        [cell("Cloud classifier"), cell("Not implemented (removed from architecture claims)")],
        [cell("IoT soil sensors"), cell("Not implemented (weather API used instead)")],
        [cell("Held-out accuracy table"), cell("Not available in repo — requires experimental run")],
        [cell("Confusion matrix"), cell("Not available in repo — requires labeled test set evaluation")],
        [cell("Usability field study"), cell("Not instrumented in code; qualitative claims deferred")],
    ]
    story.append(make_table(eval_rows, [5.0 * cm, 11.2 * cm], header=True))
    story.append(
        P(
            "<b>Table 5</b> Evaluation posture after aligning the manuscript with the codebase. "
            "Quantitative classifier metrics remain future work once a labeled test protocol is run "
            "against the packaged <tt>1.tflite</tt> model.",
            "Cap",
        )
    )

    story.append(P("5.2 Functional verification of product features", "SubHead"))
    story.append(
        P(
            "From a systems perspective, the application exposes a complete farmer workflow that can be "
            "exercised on-device: (i) onboarding and Firebase login, (ii) profile completion unlocking "
            "location-based weather, (iii) gallery-based leaf classification with uncertain-result "
            "handling, (iv) Gemini chat advisory, (v) Farm Guide browsing, and (vi) FCM-backed local "
            "alert inbox. These capabilities match the product description in the project README: "
            "AI-powered pest/disease identification, Farmer.ai-style advice, weather awareness, and "
            "localized alert notifications.",
            "BodyJustFirst",
        )
    )

    story.append(P("5.3 Comparison with the previous manuscript draft", "SubHead"))
    cmp_rows = [
        [cell("Claim in prior draft", True), cell("Updated to match code", True)],
        [cell("Hybrid on-device + cloud CNN"), cell("On-device TFLite only (AutoModel1 / 1.tflite)")],
        [cell("Crops: tomato, rice, maize, cotton"), cell("14 crops / 38 classes (Table 2)")],
        [cell("Model size 8.9 MB / 94.3 MB"), cell("Single model 12.35 MB")],
        [cell("Accuracy 91.4% / 94.7% (illustrative)"), cell("Metrics withheld pending real evaluation")],
        [cell("IoT soil moisture + crop calendar rules"), cell("WeatherAPI + FCM alerts + Room")],
        [cell("Structured multilingual disease KB"), cell("Gemini chat + 19-item Farm Guide")],
        [cell("Tomato-only confusion matrix figure"), cell("Removed; multi-crop label space documented")],
    ]
    story.append(make_table(cmp_rows, [7.5 * cm, 8.7 * cm], header=True))
    story.append(
        P(
            "<b>Table 6</b> Mapping from prior placeholder claims to implementation-accurate statements.",
            "Cap",
        )
    )

    story.append(P("6 Discussion", "SecHead"))
    story.append(
        P(
            "Aligning the manuscript with the codebase clarifies both strengths and gaps. Strengths "
            "include a shippable offline classifier, a modern Compose UI, integrated authentication, "
            "conversational advisory via a strong LLM, and practical weather/alert features for rural "
            "users. Several limitations follow directly from the implementation:",
            "BodyJustFirst",
        )
    )
    lims = [
        "Classifier quality depends on the (external) training regime of <tt>1.tflite</tt>; without checked-in evaluation, reported field accuracy cannot yet be claimed.",
        "Gallery pick (not camera-guided capture with framing overlays) is the current image input path, which may reduce control over capture quality.",
        "Per-disease treatment cards are not automatically linked to the 38 labels; farmers currently rely on Gemini free-text advice or the general Farm Guide.",
        "No soil-sensor or irrigation-scheduling engine is present; “precision farming” support is weather- and notification-centric.",
        "LLM advisory requires network access and a valid Gemini API key; pure offline advisory is limited to the static Farm Guide.",
        "Language is stored on the user profile but end-to-end localized UI/advisory translation is not fully elaborated in code.",
    ]
    for i, L in enumerate(lims, 1):
        story.append(Paragraph(f"{i}. {L}", styles["BulletBody"]))
        story.append(Spacer(1, 3))

    story.append(P("7 Conclusion", "SecHead"))
    story.append(
        P(
            "This paper presented SmartPest as realized in the project source: an AI-powered Android "
            "application that integrates on-device TensorFlow Lite disease detection over a 38-class "
            "multi-crop taxonomy, Gemini-based conversational advisory, a curated farm guide, "
            "WeatherAPI-powered conditions, and Firebase authentication and alerts. By replacing "
            "illustrative dataset and accuracy placeholders with implementation-derived tables, the "
            "manuscript now accurately reflects the deployed system. Future work includes running a "
            "formal held-out evaluation of <tt>1.tflite</tt>, linking each disease label to offline "
            "treatment guidance, optional camera-guided capture, broader localization, and—if "
            "connectivity and cost allow—selective cloud refinement or federated updates while "
            "preserving the offline-first classification path.",
            "BodyJustFirst",
        )
    )

    story.append(P("Supplementary information. Not applicable.", "BodyJustFirst"))
    story.append(
        P(
            "Acknowledgements. The authors thank participating farmers and agricultural extension "
            "officers for feedback during application prototyping.",
            "BodyJust",
        )
    )

    story.append(P("Declarations", "SecHead"))
    decls = [
        ("Funding.", "Not applicable."),
        ("Conflict of interest.", "The authors declare no conflict of interest."),
        ("Ethics approval and consent to participate.", "Not applicable."),
        ("Consent for publication.", "Not applicable."),
        (
            "Data availability.",
            "The disease-class taxonomy is listed in Table 2 and in TensorFlowHelper.kt. "
            "The packaged model artifact is app/src/main/ml/1.tflite. External training corpora "
            "(e.g., PlantVillage-style collections) should be cited when evaluation datasets are finalized.",
        ),
        ("Code availability.", "SmartPest Android application source under com.example.smartpest."),
        (
            "Author contribution.",
            "All authors contributed to system design, implementation alignment, and manuscript preparation. "
            "All authors read and approved the final manuscript.",
        ),
    ]
    for h, b in decls:
        story.append(P(f"<b>{h}</b> {b}", "BodyJustFirst"))

    story.append(P("References", "SecHead"))
    refs = [
        "Chen Y, Li X, Zhao W (2020) Edge-deployed lightweight convolutional networks for real-time crop disease recognition on smartphones. IEEE Access 8:154678–154690",
        "Das S, Bhattacharya S (2019) A survey of image processing techniques for automated plant disease diagnosis. International Journal of Computer Applications 178:1–9",
        "Ferentinos KP (2018) Deep learning models for plant disease detection and diagnosis. Computers and Electronics in Agriculture 145:311–318",
        "Food and Agriculture Organization (2021) Digital agriculture and the future of smallholder farming. FAO Technical Report Series",
        "Gonzalez M, Fernandez L (2019) Precision farming and the internet of things: opportunities and challenges. Sensors 19(8):1832",
        "Kumar N, Singh A, Rao T (2021) Convolutional neural network based classification of tomato leaf diseases using transfer learning. Computers and Electronics in Agriculture 184:106–118",
        "Mohanty SP, Hughes DP, Salathe M (2016) Using deep learning for image-based plant disease detection. Frontiers in Plant Science 7:1419",
        "Nguyen T, Pham H (2022) Vision transformers for fine-grained plant disease classification. In: Proceedings of the Conference on Computer Vision in Agriculture, pp 88–97",
        "Oyewola D, Dada E (2021) Machine learning based advisory systems for smallholder crop management. Artificial Intelligence in Agriculture 5:23–34",
        "Patil R, Deshmukh S (2020) A mobile-based expert system for crop disease diagnosis in smallholder farms. In: Proceedings of the International Conference on Smart Agriculture, pp 201–209",
        "Ramesh S, Iyer V, Krishnan P (2023) Design of a multilingual voice-based advisory chatbot for farmers using natural language processing. Journal of Rural Computing and Development 9(1):55–70",
    ]
    for r in refs:
        story.append(Paragraph(r, styles["Ref"]))

    story.append(Spacer(1, 16))
    story.append(HRFlowable(width="100%", thickness=0.5, color=colors.grey, spaceAfter=6))
    story.append(
        P(
            "Document note: This revision updates sn-article.pdf placeholder tables and architecture "
            "claims using the SmartPest Android project (TensorFlowHelper 38-class list; 1.tflite size "
            "12.35 MB; τ = 0.7; Gemini 1.5 Flash; WeatherAPI; Firebase Auth/FCM; Room; Farm Guide 19 topics). "
            "Illustrative accuracy/latency/confusion-matrix figures were removed rather than fabricated.",
            "FooterNote",
        )
    )
    return story


def write_pdf(path: Path, story, styles):
    path.parent.mkdir(parents=True, exist_ok=True)
    doc = SimpleDocTemplate(
        str(path),
        pagesize=A4,
        leftMargin=25 * mm,
        rightMargin=25 * mm,
        topMargin=22 * mm,
        bottomMargin=22 * mm,
        title=(
            "SmartPest: An AI-Powered Mobile System for Crop Disease Detection, "
            "Advisory Support, and Precision Farming Assistance"
        ),
        author="SmartPest Project",
    )

    def add_page_number(canvas, doc_):
        canvas.saveState()
        canvas.setFont("Times-Roman", 9)
        canvas.drawCentredString(page_w / 2, 12 * mm, str(canvas.getPageNumber()))
        canvas.restoreState()

    doc.build(story, onFirstPage=add_page_number, onLaterPages=add_page_number)
    print(f"Wrote {path} ({path.stat().st_size} bytes)")


def main():
    styles = build_styles()
    story = build_story(styles)
    write_pdf(OUT, story, styles)
    write_pdf(OUT_PROJECT, list(build_story(styles)), styles)


if __name__ == "__main__":
    main()
