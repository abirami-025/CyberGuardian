package com.cyberguardian.domain.model

import java.util.UUID

/**
 * Risk level classification for detected threats
 */
enum class RiskLevel(val score: Int, val label: String) {
    SAFE(0, "Safe"),
    LOW(25, "Low Risk"),
    MEDIUM(50, "Medium Risk"),
    HIGH(75, "High Risk"),
    BLOCKED(100, "Blocked")
}

/**
 * Type of cyber threat detected
 */
enum class ThreatType(val displayName: String, val icon: String) {
    PHISHING("Phishing", "🎣"),
    SMISHING("SMS Scam", "💬"),
    VISHING("Voice Fraud", "📞"),
    UPI_FRAUD("UPI Fraud", "💸"),
    OTP_THEFT("OTP Theft", "🔑"),
    DEEPFAKE("Deepfake", "🎭"),
    MALICIOUS_URL("Malicious Link", "🔗"),
    QR_PHISHING("QR Phishing", "📱"),
    IDENTITY_THEFT("Identity Theft", "🆔"),
    SOCIAL_ENGINEERING("Social Engineering", "🎯"),
    MALWARE("Malware", "🦠"),
    DATA_BREACH("Data Breach", "💾"),
    MEDIA_EXPLOIT("Media Exploit", "📎"),
    UNKNOWN("Unknown Threat", "⚠️")
}

/**
 * Source of the threat detection
 */
enum class ThreatSource {
    SMS, NOTIFICATION, CALL, URL, QR_CODE, FILE, MANUAL_SCAN, BREACH_CHECK
}

/**
 * Core threat model
 */
data class Threat(
    val id: String = UUID.randomUUID().toString(),
    val type: ThreatType,
    val riskLevel: RiskLevel,
    val source: ThreatSource,
    val title: String,
    val description: String,
    val explanation: String,  // User-friendly explanation of why it's risky
    val actionTaken: String? = null,
    val senderInfo: String? = null,
    val contentPreview: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isNeutralized: Boolean = false,
    val userFeedback: UserFeedback? = null
)

/**
 * User feedback on threat detection accuracy
 */
enum class UserFeedback {
    ACCURATE, FALSE_POSITIVE, FALSE_NEGATIVE
}

/**
 * Dashboard statistics
 */
data class GuardianStats(
    val threatsBlocked: Int = 0,
    val messagesScanned: Int = 0,
    val callsAnalyzed: Int = 0,
    val linksChecked: Int = 0,
    val trustScore: Float = 100f,
    val currentRiskLevel: RiskLevel = RiskLevel.SAFE,
    val isGuardianActive: Boolean = true
)

/**
 * Scan result from analyzing content
 */
data class ScanResult(
    val content: String,
    val riskLevel: RiskLevel,
    val threats: List<ThreatIndicator>,
    val explanation: String,
    val suggestedAction: SuggestedAction,
    val confidence: Float  // 0.0 to 1.0
)

/**
 * Individual threat indicator found in content
 */
data class ThreatIndicator(
    val type: ThreatType,
    val pattern: String,
    val confidence: Float,
    val detail: String
)

/**
 * Suggested action for a detected threat
 */
enum class SuggestedAction(val label: String, val description: String) {
    IGNORE("Ignore", "This appears safe — no action needed."),
    REVIEW("Review", "Take a closer look before proceeding."),
    BLOCK("Block", "We recommend blocking this sender."),
    DELETE("Delete", "Delete this message for safety."),
    REPORT("Report", "Report this to authorities."),
    CHANGE_PASSWORD("Change Password", "Update your passwords immediately.")
}

/**
 * Advisor chat message
 */
data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false
)

/**
 * Breach check result
 */
data class BreachResult(
    val query: String,
    val isBreached: Boolean,
    val breachCount: Int = 0,
    val breaches: List<BreachInfo> = emptyList(),
    val checkedAt: Long = System.currentTimeMillis()
)

data class BreachInfo(
    val name: String,
    val date: String,
    val dataTypes: List<String>,
    val description: String
)

/**
 * Emergency action
 */
data class EmergencyAction(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val actionType: EmergencyActionType
)

enum class EmergencyActionType {
    CALL_HELPLINE, REPORT_WEBSITE, BLOCK_SENDER, FLAG_UPI
}
