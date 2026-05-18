package com.cyberguardian.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberguardian.data.repository.ThreatRepository
import com.cyberguardian.domain.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val threatRepository: ThreatRepository
) : ViewModel() {

    private val _stats = MutableStateFlow(
        GuardianStats(
            threatsBlocked = 47,
            messagesScanned = 1284,
            callsAnalyzed = 156,
            linksChecked = 312,
            trustScore = 94.5f,
            currentRiskLevel = RiskLevel.LOW,
            isGuardianActive = true
        )
    )
    val stats: StateFlow<GuardianStats> = _stats.asStateFlow()

    private val _recentThreats = MutableStateFlow<List<Threat>>(emptyList())
    val recentThreats: StateFlow<List<Threat>> = _recentThreats.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    init {
        loadRecentThreats()
        loadMockData()
    }

    private fun loadRecentThreats() {
        viewModelScope.launch {
            threatRepository.getRecentThreats(5).collect { threats ->
                if (threats.isNotEmpty()) {
                    _recentThreats.value = threats
                }
            }
        }
    }

    private fun loadMockData() {
        // Provide realistic demo data for initial experience
        _recentThreats.value = listOf(
            Threat(
                type = ThreatType.UPI_FRAUD,
                riskLevel = RiskLevel.BLOCKED,
                source = ThreatSource.SMS,
                title = "UPI Payment Scam Blocked",
                description = "Fraudulent UPI collect request from unknown merchant",
                explanation = "This message contained a fake UPI payment request with urgency tactics. Legitimate merchants don't send threatening payment links via SMS.",
                senderInfo = "+91-98xxx-xxxxx",
                isNeutralized = true,
                timestamp = System.currentTimeMillis() - 3600000
            ),
            Threat(
                type = ThreatType.OTP_THEFT,
                riskLevel = RiskLevel.HIGH,
                source = ThreatSource.SMS,
                title = "OTP Theft Attempt Detected",
                description = "Someone tried to extract your banking OTP",
                explanation = "A message asked you to share your OTP. Banks and services NEVER ask for OTPs. This was a social engineering attempt.",
                senderInfo = "VM-FAKEBNK",
                isNeutralized = true,
                timestamp = System.currentTimeMillis() - 7200000
            ),
            Threat(
                type = ThreatType.PHISHING,
                riskLevel = RiskLevel.HIGH,
                source = ThreatSource.URL,
                title = "Phishing Link Blocked",
                description = "Fake SBI login page detected",
                explanation = "This link redirects to a page that looks like SBI but is hosted on a suspicious domain. It was designed to steal your login credentials.",
                contentPreview = "http://sbi-secure-login.xyz/verify",
                isNeutralized = true,
                timestamp = System.currentTimeMillis() - 86400000
            ),
            Threat(
                type = ThreatType.SMISHING,
                riskLevel = RiskLevel.MEDIUM,
                source = ThreatSource.SMS,
                title = "Prize Scam SMS",
                description = "Fake lottery/prize notification",
                explanation = "This is a classic 'You've won' scam. You cannot win a lottery you didn't enter. The link would collect your personal and financial details.",
                senderInfo = "+91-70xxx-xxxxx",
                isNeutralized = true,
                timestamp = System.currentTimeMillis() - 172800000
            )
        )
    }

    fun toggleScanning() {
        viewModelScope.launch {
            _isScanning.value = true
            kotlinx.coroutines.delay(3000)
            _isScanning.value = false
        }
    }
}
