package com.example.agristation1.network.gemini

import kotlinx.serialization.Serializable

@Serializable
data class GeminiRequest(
    val systemInstruction: GeminiContent,
    val contents: List<GeminiContent>
)

@Serializable
data class GeminiContent(
    val role: String,
    val parts: List<GeminiPart>
)

@Serializable
data class GeminiPart(
    val text: String
)

@Serializable
data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

@Serializable
data class GeminiCandidate(
    val content: GeminiContent
)