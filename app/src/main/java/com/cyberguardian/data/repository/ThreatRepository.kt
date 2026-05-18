package com.cyberguardian.data.repository

import com.cyberguardian.data.local.*
import com.cyberguardian.data.remote.*
import com.cyberguardian.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThreatRepository @Inject constructor(
    private val threatDao: ThreatDao,
    private val api: InforgeApiService
) {
    fun getAllThreats(): Flow<List<Threat>> = threatDao.getAllThreats().map { entities ->
        entities.map { it.toDomain() }
    }

    fun getRecentThreats(limit: Int = 20): Flow<List<Threat>> =
        threatDao.getRecentThreats(limit).map { entities ->
            entities.map { it.toDomain() }
        }

    fun getBlockedCount(): Flow<Int> = threatDao.getBlockedCount()
    fun getTotalCount(): Flow<Int> = threatDao.getTotalThreatCount()

    suspend fun saveThreat(threat: Threat) {
        threatDao.insertThreat(threat.toEntity())
    }

    suspend fun updateThreat(threat: Threat) {
        threatDao.updateThreat(threat.toEntity())
    }

    suspend fun scanMessage(content: String, sender: String?): ScanResult {
        return try {
            val response = api.scanMessage(MessageScanRequest(content, sender))
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.toDomain()
            } else {
                performLocalAnalysis(content)
            }
        } catch (e: Exception) {
            performLocalAnalysis(content)
        }
    }

    suspend fun scanUrl(url: String): ScanResult {
        return try {
            val response = api.scanUrl(UrlScanRequest(url))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                ScanResult(
                    content = url,
                    riskLevel = scoreToRiskLevel(body.riskScore),
                    threats = body.threats.map {
                        ThreatIndicator(ThreatType.MALICIOUS_URL, it, 0.8f, it)
                    },
                    explanation = body.explanation,
                    suggestedAction = if (body.isMalicious) SuggestedAction.BLOCK else SuggestedAction.IGNORE,
                    confidence = body.riskScore / 100f
                )
            } else {
                performLocalUrlAnalysis(url)
            }
        } catch (e: Exception) {
            performLocalUrlAnalysis(url)
        }
    }

    suspend fun scanQr(data: String): ScanResult {
        return try {
            val response = api.scanQrCode(QrScanRequest(data))
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                ScanResult(
                    content = data,
                    riskLevel = scoreToRiskLevel(body.riskScore),
                    threats = body.threats.map {
                        ThreatIndicator(ThreatType.QR_PHISHING, it, 0.8f, it)
                    },
                    explanation = body.explanation,
                    suggestedAction = if (body.isMalicious) SuggestedAction.BLOCK else SuggestedAction.IGNORE,
                    confidence = body.riskScore / 100f
                )
            } else {
                performLocalUrlAnalysis(data)
            }
        } catch (e: Exception) {
            performLocalUrlAnalysis(data)
        }
    }

    /**
     * Local fallback analysis using pattern matching for India-specific scams
     */
    private fun performLocalAnalysis(content: String): ScanResult {
        val lowerContent = content.lowercase()
        val threats = mutableListOf<ThreatIndicator>()
        var maxRisk = RiskLevel.SAFE

        // UPI fraud patterns
        val upiPatterns = listOf("upi", "gpay", "phonepe", "paytm", "bhim", "send money", "transfer")
        if (upiPatterns.any { lowerContent.contains(it) } &&
            listOf("urgent", "immediately", "expire", "block", "suspend").any { lowerContent.contains(it) }) {
            threats.add(ThreatIndicator(ThreatType.UPI_FRAUD, "UPI urgency pattern", 0.85f,
                "Message mentions UPI payment with urgency — common fraud tactic in India"))
            maxRisk = RiskLevel.HIGH
        }

        // OTP theft patterns
        if (listOf("otp", "one time password", "verification code", "pin").any { lowerContent.contains(it) } &&
            listOf("share", "send", "tell", "provide", "give").any { lowerContent.contains(it) }) {
            threats.add(ThreatIndicator(ThreatType.OTP_THEFT, "OTP request pattern", 0.9f,
                "Someone is asking for your OTP — legitimate services never ask for OTPs"))
            maxRisk = RiskLevel.BLOCKED
        }

        // Phishing link patterns
        val urlRegex = Regex("https?://[\\w.-]+")
        if (urlRegex.containsMatchIn(content)) {
            val suspiciousUrlPatterns = listOf("bit.ly", "tinyurl", "short.link", "click", "verify", "update", "secure")
            if (suspiciousUrlPatterns.any { lowerContent.contains(it) }) {
                threats.add(ThreatIndicator(ThreatType.PHISHING, "Suspicious shortened URL", 0.7f,
                    "Shortened URLs in messages can hide malicious destinations"))
                if (maxRisk.score < RiskLevel.MEDIUM.score) maxRisk = RiskLevel.MEDIUM
            }
        }

        // Prize/lottery scam
        if (listOf("congratulations", "winner", "prize", "lottery", "lakh", "crore", "reward").any { lowerContent.contains(it) } &&
            listOf("claim", "click", "call", "contact").any { lowerContent.contains(it) }) {
            threats.add(ThreatIndicator(ThreatType.SMISHING, "Prize/lottery scam", 0.88f,
                "Prize and lottery scams are extremely common — you cannot win prizes you didn't enter"))
            maxRisk = RiskLevel.HIGH
        }

        // Impersonation patterns
        if (listOf("rbi", "income tax", "police", "customs", "sbi", "hdfc", "icici", "axis bank")
                .any { lowerContent.contains(it) } &&
            listOf("action", "legal", "arrest", "block", "freeze", "suspend").any { lowerContent.contains(it) }) {
            threats.add(ThreatIndicator(ThreatType.SOCIAL_ENGINEERING, "Authority impersonation", 0.82f,
                "Government/bank authorities never threaten via SMS — verify through official channels"))
            if (maxRisk.score < RiskLevel.HIGH.score) maxRisk = RiskLevel.HIGH
        }

        // KYC fraud
        if (listOf("kyc", "pan", "aadhaar", "aadhar").any { lowerContent.contains(it) } &&
            listOf("expire", "update", "verify", "link", "mandatory").any { lowerContent.contains(it) }) {
            threats.add(ThreatIndicator(ThreatType.PHISHING, "KYC/document fraud", 0.8f,
                "Fake KYC update requests are common in India — update KYC only through official bank apps"))
            if (maxRisk.score < RiskLevel.HIGH.score) maxRisk = RiskLevel.HIGH
        }

        val explanation = if (threats.isEmpty()) {
            "This message appears safe. No known scam patterns detected."
        } else {
            threats.joinToString(". ") { it.detail }
        }

        val action = when (maxRisk) {
            RiskLevel.BLOCKED -> SuggestedAction.BLOCK
            RiskLevel.HIGH -> SuggestedAction.REPORT
            RiskLevel.MEDIUM -> SuggestedAction.REVIEW
            else -> SuggestedAction.IGNORE
        }

        return ScanResult(content, maxRisk, threats, explanation, action,
            if (threats.isEmpty()) 0.95f else threats.maxOf { it.confidence })
    }

    private fun performLocalUrlAnalysis(url: String): ScanResult {
        val lowerUrl = url.lowercase()
        val suspicious = listOf("login", "verify", "secure", "update", "account", "bank", "upi", "pay")
            .count { lowerUrl.contains(it) }
        val isShortened = listOf("bit.ly", "tinyurl", "goo.gl", "t.co", "short").any { lowerUrl.contains(it) }

        val risk = when {
            suspicious >= 3 -> RiskLevel.HIGH
            suspicious >= 2 || isShortened -> RiskLevel.MEDIUM
            suspicious >= 1 -> RiskLevel.LOW
            else -> RiskLevel.SAFE
        }

        return ScanResult(
            content = url,
            riskLevel = risk,
            threats = if (risk != RiskLevel.SAFE) listOf(
                ThreatIndicator(ThreatType.MALICIOUS_URL, "Suspicious URL pattern", 0.6f,
                    "This URL contains patterns commonly found in phishing sites")
            ) else emptyList(),
            explanation = if (risk == RiskLevel.SAFE) "URL appears safe" else "URL contains suspicious patterns — exercise caution",
            suggestedAction = if (risk.score >= RiskLevel.HIGH.score) SuggestedAction.BLOCK else SuggestedAction.REVIEW,
            confidence = 0.6f
        )
    }

    private fun scoreToRiskLevel(score: Int): RiskLevel = when {
        score >= 80 -> RiskLevel.BLOCKED
        score >= 60 -> RiskLevel.HIGH
        score >= 40 -> RiskLevel.MEDIUM
        score >= 20 -> RiskLevel.LOW
        else -> RiskLevel.SAFE
    }
}

// ── Extension mappers ──

fun ThreatEntity.toDomain() = Threat(
    id = id,
    type = ThreatType.valueOf(type),
    riskLevel = RiskLevel.valueOf(riskLevel),
    source = ThreatSource.valueOf(source),
    title = title,
    description = description,
    explanation = explanation,
    actionTaken = actionTaken,
    senderInfo = senderInfo,
    contentPreview = contentPreview,
    timestamp = timestamp,
    isNeutralized = isNeutralized,
    userFeedback = userFeedback?.let { UserFeedback.valueOf(it) }
)

fun Threat.toEntity() = ThreatEntity(
    id = id,
    type = type.name,
    riskLevel = riskLevel.name,
    source = source.name,
    title = title,
    description = description,
    explanation = explanation,
    actionTaken = actionTaken,
    senderInfo = senderInfo,
    contentPreview = contentPreview,
    timestamp = timestamp,
    isNeutralized = isNeutralized,
    userFeedback = userFeedback?.name
)

fun MessageScanResponse.toDomain() = ScanResult(
    content = "",
    riskLevel = when {
        riskScore >= 80 -> RiskLevel.BLOCKED
        riskScore >= 60 -> RiskLevel.HIGH
        riskScore >= 40 -> RiskLevel.MEDIUM
        riskScore >= 20 -> RiskLevel.LOW
        else -> RiskLevel.SAFE
    },
    threats = threats.map {
        ThreatIndicator(
            type = try { ThreatType.valueOf(it.type) } catch (e: Exception) { ThreatType.UNKNOWN },
            pattern = it.pattern,
            confidence = it.confidence,
            detail = it.detail
        )
    },
    explanation = explanation,
    suggestedAction = try { SuggestedAction.valueOf(suggestedAction) } catch (e: Exception) { SuggestedAction.REVIEW },
    confidence = confidence
)
