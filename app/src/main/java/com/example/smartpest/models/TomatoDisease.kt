package com.example.smartpest.models

/** Class indices are inherited from the original app, not from model metadata (none is bundled). */
data class TomatoDisease(
    val modelIndex: Int,
    val displayName: String,
    val category: String,
    val summary: String,
    val symptoms: List<String>,
    val actions: List<String>,
    val referenceUrl: String,
    val urgent: Boolean = false
) {
    companion object {
        private const val UC_TOMATO = "https://ipm.ucanr.edu/agriculture/tomato/"
        val entries: List<TomatoDisease> = listOf(
            TomatoDisease(28, "Bacterial spot", "Disease",
                "A bacterial disease that can affect tomato leaves and fruit, especially in warm, wet conditions.",
                listOf("Small, dark, water-soaked spots, sometimes with yellow edges.", "Spots may merge; affected leaves can yellow and drop."),
                listOf("Avoid handling wet plants and water at soil level to limit splash.", "Remove affected debris and sanitize tools between plants.", "Use disease-free transplants. Ask a local extension adviser about registered controls; resistance can limit their effectiveness."),
                UC_TOMATO + "bacterial-spot/"),
            TomatoDisease(29, "Early blight", "Disease",
                "A fungal disease often noticed first on older, lower tomato leaves.",
                listOf("Brown spots with concentric, target-like rings.", "Yellowing around spots, often starting on lower leaves."),
                listOf("Remove badly affected lower leaves when dry; avoid stripping the plant.", "Mulch the soil, stake plants and water at the base to reduce splash.", "Rotate away from tomatoes and potatoes where practical. Confirm the diagnosis before selecting a locally registered fungicide."),
                "https://ipm.cahnr.uconn.edu/tomato-early-blight/"),
            TomatoDisease(30, "Late blight", "Disease",
                "A potentially fast-spreading disease that needs prompt local confirmation.",
                listOf("Irregular water-soaked patches that turn brown or dark.", "White growth may appear beneath leaf lesions in humid conditions; stems and fruit may also be affected."),
                listOf("Separate suspect plants where possible and contact a local plant clinic promptly.", "Avoid moving wet plants or affected material through the growing area.", "If confirmed, follow local advice for removing affected plants and protecting nearby tomatoes. Do not rely on a photo result alone."),
                UC_TOMATO + "late-blight/", urgent = true),
            TomatoDisease(31, "Leaf mold", "Disease",
                "A fungal disease favored by high humidity, especially in protected growing spaces.",
                listOf("Pale yellow patches on the upper leaf surface.", "Olive-green to brown, velvety mold beneath affected patches."),
                listOf("Improve ventilation and spacing; reduce prolonged high humidity.", "Water at the soil and avoid leaving foliage wet overnight.", "Remove infected debris and choose resistant tomato varieties for future plantings."),
                "https://extension.umn.edu/disease-management/tomato-leaf-mold"),
            TomatoDisease(32, "Septoria leaf spot", "Disease",
                "A fungal leaf disease that commonly starts on lower tomato foliage.",
                listOf("Many small, round spots with pale centers and darker borders.", "Tiny black dots may be visible in spot centers; lower leaves yellow and drop."),
                listOf("Remove badly affected lower foliage when dry and dispose of debris away from plants.", "Mulch, support plants and water at soil level to reduce soil splash.", "Rotate planting sites and seek local advice if disease keeps spreading."),
                "https://ipm.cahnr.uconn.edu/fungal-leaf-fruit-spots-of-tomatoes-i/"),
            TomatoDisease(33, "Two-spotted spider mite damage", "Pest damage",
                "Feeding damage from mites, not an infectious plant disease. Inspect the underside of the leaf to confirm.",
                listOf("Fine pale stippling, bronzing or drying leaves.", "Tiny moving mites and fine webbing, usually on leaf undersides."),
                listOf("Check leaf undersides with a hand lens before treating.", "Reduce dust and water stress; preserve beneficial predatory mites.", "Ask a local adviser about tomato-safe controls if needed. Avoid broad-spectrum sprays that can worsen mite outbreaks."),
                "https://ipm.ucanr.edu/home-and-landscape/spider-mites/"),
            TomatoDisease(34, "Target spot", "Disease",
                "A fungal disease whose circular lesions can resemble early blight.",
                listOf("Brown circular spots, sometimes with concentric rings and yellow halos.", "Lesions may expand and merge; fruit can also develop sunken spots."),
                listOf("Improve airflow and keep foliage dry by irrigating at soil level.", "Remove infected debris and avoid working through wet foliage.", "Have an adviser distinguish target spot from similar diseases before choosing registered controls."),
                UC_TOMATO),
            TomatoDisease(35, "Tomato yellow leaf curl virus", "Disease",
                "A viral disease spread by whiteflies. Leaf symptoms alone are not definitive.",
                listOf("Young leaves curl upward, with yellowing at the edges.", "Plants may become stunted and set fewer flowers or fruit."),
                listOf("There is no cure for an infected plant; seek confirmation before removing it.", "Monitor whiteflies and follow local integrated pest management advice.", "Use healthy transplants and resistant varieties, and control host weeds around the planting."),
                UC_TOMATO + "tomato-yellow-leaf-curl/"),
            TomatoDisease(36, "Tomato mosaic virus", "Disease",
                "A viral disease that can spread through contaminated hands, tools and plant material.",
                listOf("Mottled light and dark green, mosaic-like leaf patterns.", "Leaves may be distorted or narrow; growth and fruit can be affected."),
                listOf("There is no cure for an infected plant; confirm the cause with a plant clinic.", "Wash hands and disinfect tools using locally recommended procedures between plants.", "Use tested seed and healthy transplants. Avoid handling tomato plants after contact with tobacco products."),
                UC_TOMATO),
            TomatoDisease(37, "Healthy-looking leaf", "Healthy",
                "The photo most closely matches the model's healthy tomato category. This does not rule out disease, nutrient problems or conditions outside the model.",
                listOf("The model did not identify a strong match to its supported damage categories in this photo."),
                listOf("Continue checking both sides of leaves and monitor new growth.", "Water consistently at soil level and keep tools clean.", "If symptoms persist elsewhere on the plant, photograph the affected area and consult a local adviser."),
                UC_TOMATO)
        )

        fun search(query: String): List<TomatoDisease> {
            val term = query.trim()
            if (term.isEmpty()) return entries
            return entries.filter { disease ->
                (listOf(disease.displayName, disease.category, disease.summary) + disease.symptoms)
                    .any { it.contains(term, ignoreCase = true) }
            }
        }
    }
}
