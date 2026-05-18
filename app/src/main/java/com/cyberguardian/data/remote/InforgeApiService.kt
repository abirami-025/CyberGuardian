package com.cyberguardian.data.remote

import retrofit2.Response
import retrofit2.http.*

/**
 * Inforge API service for real-time threat intelligence.
 * Replace base URL and API key with real credentials.
 */
interface InforgeApiService {

    /**
     * Check a URL/link for malicious content
     */
    @POST("scan/url")
    suspend fun scanUrl(
        @Body request: UrlScanRequest
    ): Response<UrlScanResponse>

    /**
     * Analyze SMS/message content for scam patterns
     */
    @POST("scan/message")
    suspend fun scanMessage(
        @Body request: MessageScanRequest
    ): Response<MessageScanResponse>

    /**
     * Check QR code data for threats
     */
    @POST("scan/qr")
    suspend fun scanQrCode(
        @Body request: QrScanRequest
    ): Response<QrScanResponse>

    /**
     * Check email/phone against breach databases
     */
    @POST("breach/check")
    suspend fun checkBreach(
        @Body request: BreachCheckRequest
    ): Response<BreachCheckResponse>

    /**
     * Get latest threat feed (India-specific patterns)
     */
    @GET("feeds/threats")
    suspend fun getThreatFeed(
        @Query("region") region: String = "IN",
        @Query("limit") limit: Int = 50
    ): Response<ThreatFeedResponse>

    /**
     * AI advisor query
     */
    @POST("advisor/query")
    suspend fun queryAdvisor(
        @Body request: AdvisorRequest
    ): Response<AdvisorResponse>

    /**
     * Analyze call transcript for fraud patterns
     */
    @POST("scan/call")
    suspend fun analyzeCall(
        @Body request: CallAnalysisRequest
    ): Response<CallAnalysisResponse>
}

// ── Request/Response Models ──

data class UrlScanRequest(val url: String, val context: String? = null)
data class UrlScanResponse(
    val url: String,
    val isMalicious: Boolean,
    val riskScore: Int,
    val threats: List<String>,
    val explanation: String,
    val category: String?
)

data class MessageScanRequest(
    val content: String,
    val sender: String? = null,
    val messageType: String = "sms"
)
data class MessageScanResponse(
    val riskScore: Int,
    val threats: List<ThreatDetail>,
    val explanation: String,
    val suggestedAction: String,
    val confidence: Float
)

data class ThreatDetail(
    val type: String,
    val pattern: String,
    val confidence: Float,
    val detail: String
)

data class QrScanRequest(val data: String, val source: String = "camera")
data class QrScanResponse(
    val data: String,
    val isMalicious: Boolean,
    val riskScore: Int,
    val threats: List<String>,
    val explanation: String,
    val upiValidation: UpiValidation?
)

data class UpiValidation(
    val isValid: Boolean,
    val merchantName: String?,
    val amount: String?,
    val mismatchWarning: String?
)

data class BreachCheckRequest(val identifier: String, val type: String = "email")
data class BreachCheckResponse(
    val isBreached: Boolean,
    val breachCount: Int,
    val breaches: List<BreachDetail>
)

data class BreachDetail(
    val name: String,
    val date: String,
    val dataTypes: List<String>,
    val description: String
)

data class ThreatFeedResponse(
    val threats: List<FeedThreat>,
    val lastUpdated: Long,
    val region: String
)

data class FeedThreat(
    val id: String,
    val type: String,
    val pattern: String,
    val severity: String,
    val description: String,
    val indicators: List<String>
)

data class AdvisorRequest(
    val query: String,
    val context: String? = null,
    val threatId: String? = null
)
data class AdvisorResponse(
    val response: String,
    val suggestions: List<String>,
    val relatedThreats: List<String>
)

data class CallAnalysisRequest(
    val transcript: String,
    val callerNumber: String?,
    val duration: Long
)
data class CallAnalysisResponse(
    val riskScore: Int,
    val threats: List<ThreatDetail>,
    val isDeepfake: Boolean,
    val deepfakeConfidence: Float,
    val explanation: String
)
