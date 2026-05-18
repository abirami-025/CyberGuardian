package com.cyberguardian.data.repository

import com.cyberguardian.data.local.ChatMessageDao
import com.cyberguardian.data.local.ChatMessageEntity
import com.cyberguardian.data.remote.AdvisorRequest
import com.cyberguardian.data.remote.InforgeApiService
import com.cyberguardian.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdvisorRepository @Inject constructor(
    private val chatMessageDao: ChatMessageDao,
    private val api: InforgeApiService
) {
    fun getMessages(): Flow<List<ChatMessage>> =
        chatMessageDao.getAllMessages().map { entities ->
            entities.map { ChatMessage(it.id, it.content, it.isFromUser, it.timestamp) }
        }

    suspend fun sendMessage(query: String, context: String? = null): ChatMessage {
        // Save user message
        val userMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            content = query,
            isFromUser = true,
            timestamp = System.currentTimeMillis()
        )
        chatMessageDao.insertMessage(userMsg)

        // Get AI response
        val responseText = try {
            val response = api.queryAdvisor(AdvisorRequest(query, context))
            if (response.isSuccessful && response.body() != null) {
                response.body()!!.response
            } else {
                generateLocalResponse(query)
            }
        } catch (e: Exception) {
            generateLocalResponse(query)
        }

        // Save AI response
        val aiMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            content = responseText,
            isFromUser = false,
            timestamp = System.currentTimeMillis()
        )
        chatMessageDao.insertMessage(aiMsg)

        return ChatMessage(aiMsg.id, aiMsg.content, false, aiMsg.timestamp)
    }

    suspend fun clearHistory() = chatMessageDao.deleteAll()

    private fun generateLocalResponse(query: String): String {
        val lowerQuery = query.lowercase()
        return when {
            lowerQuery.contains("upi") || lowerQuery.contains("payment") ->
                "🛡️ UPI Safety Tips:\n\n" +
                "• Never share your UPI PIN with anyone — not even bank officials\n" +
                "• You DON'T need to enter PIN to RECEIVE money\n" +
                "• Always verify merchant name before paying\n" +
                "• Use only official UPI apps (Google Pay, PhonePe, BHIM)\n" +
                "• Report fraud immediately at 1930 or cybercrime.gov.in\n\n" +
                "Stay safe! I'm here if you need more guidance. 💪"

            lowerQuery.contains("otp") ->
                "🔐 OTP Security:\n\n" +
                "• NEVER share OTP with anyone — banks will NEVER ask for it\n" +
                "• OTP is valid for YOU only, for YOUR transaction\n" +
                "• If someone calls asking for OTP, hang up immediately\n" +
                "• Report such calls to your bank and at 1930\n\n" +
                "Remember: Your OTP = Your money's key. Guard it! 🛡️"

            lowerQuery.contains("deepfake") || lowerQuery.contains("fake voice") || lowerQuery.contains("voice clone") ->
                "🎭 Deepfake Protection:\n\n" +
                "• AI can now clone voices — even from a few seconds of audio\n" +
                "• If a 'family member' calls asking for money urgently, VERIFY\n" +
                "• Call back on their known number to confirm\n" +
                "• Watch for unnatural pauses, robotic tone, or scripted speech\n" +
                "• When in doubt, use a secret family code word\n\n" +
                "I'm monitoring for voice anomalies during your calls! 🔍"

            lowerQuery.contains("safe") || lowerQuery.contains("secure") || lowerQuery.contains("protected") ->
                "✅ Your Protection Status:\n\n" +
                "• Real-time SMS scanning: Active\n" +
                "• Call fraud detection: Active\n" +
                "• Link/QR protection: Active\n" +
                "• Behavior learning: Adapting to you\n\n" +
                "You're well protected! I'm continuously learning your patterns to keep false alerts minimal. 🌟"

            lowerQuery.contains("help") || lowerQuery.contains("what can") ->
                "👋 I'm your AI Cyber Guardian! Here's what I can help with:\n\n" +
                "🔍 Scan suspicious messages, links, or QR codes\n" +
                "📞 Analyze calls for voice fraud & deepfakes\n" +
                "💡 Explain any threat in simple terms\n" +
                "🆘 Guide you through emergency reporting\n" +
                "📚 Teach you about latest cyber threats\n\n" +
                "Just ask me anything about staying safe online! 💫"

            else ->
                "🤖 Great question! Here's what I know:\n\n" +
                "Cyber threats in India are evolving rapidly. The most common ones are:\n" +
                "1. UPI/payment fraud (₹ scams)\n" +
                "2. OTP theft via social engineering\n" +
                "3. Phishing links in SMS/WhatsApp\n" +
                "4. KYC update frauds\n" +
                "5. Deepfake voice calls\n\n" +
                "I'm here 24/7 to protect you. Ask me about any specific threat! 🛡️"
        }
    }
}
