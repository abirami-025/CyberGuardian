package com.cyberguardian.ui.screens.advisor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberguardian.data.repository.AdvisorRepository
import com.cyberguardian.domain.model.ChatMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdvisorViewModel @Inject constructor(
    private val advisorRepository: AdvisorRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage(
            content = "👋 Hey! I'm your AI Cyber Guardian.\n\n" +
                    "I'm here to help you stay safe online. Ask me anything about:\n" +
                    "• UPI & payment safety\n" +
                    "• Suspicious messages or calls\n" +
                    "• OTP & password security\n" +
                    "• Deepfake detection\n" +
                    "• Any cyber threat\n\n" +
                    "What's on your mind? 🛡️",
            isFromUser = false
        )
    ))
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    init {
        viewModelScope.launch {
            advisorRepository.getMessages().collect { dbMessages ->
                if (dbMessages.isNotEmpty()) {
                    _messages.value = _messages.value.take(1) + dbMessages
                }
            }
        }
    }

    fun sendMessage(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            val userMsg = ChatMessage(content = query, isFromUser = true)
            _messages.value = _messages.value + userMsg
            _isTyping.value = true
            try {
                val response = advisorRepository.sendMessage(query)
                _messages.value = _messages.value + response
            } catch (e: Exception) {
                val errorMsg = ChatMessage(
                    content = "I'm having trouble connecting right now. Please try again in a moment. 🔄",
                    isFromUser = false
                )
                _messages.value = _messages.value + errorMsg
            }
            _isTyping.value = false
        }
    }
}
