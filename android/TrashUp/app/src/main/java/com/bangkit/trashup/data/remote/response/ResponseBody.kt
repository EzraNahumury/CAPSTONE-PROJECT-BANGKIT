@file:Suppress("unused", "unused", "unused", "unused", "unused", "unused")

package com.bangkit.trashup.data.remote.response

import com.bangkit.trashup.data.remote.request.Content

typealias ResponseBody = ArrayList<ResponseBodyElement>

data class ResponseBodyElement (
    val candidates: List<Candidate>,
    val modelVersion: ModelVersion,
    val usageMetadata: UsageMetadata? = null
)

data class Candidate (
    val content: Content,
    val safetyRatings: List<SafetyRating>? = null,
    val finishReason: String? = null
)


enum class Role {
    Model
}

data class SafetyRating (
    val category: Category,
    val probability: Probability,
    val probabilityScore: Double,
    val severity: Severity,
    val severityScore: Double
)

@Suppress("unused", "unused")
enum class Category {
    HarmCategoryDangerousContent,
    HarmCategoryHarassment,
    HarmCategoryHateSpeech,
    HarmCategorySexuallyExplicit
}

enum class Probability {
    Negligible
}

enum class Severity {
    HarmSeverityLow,
    HarmSeverityNegligible
}

enum class ModelVersion {
    Gemini15Pro001
}

data class UsageMetadata (
    val promptTokenCount: Long,
    val candidatesTokenCount: Long,
    val totalTokenCount: Long
)