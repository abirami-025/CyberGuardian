package com.cyberguardian.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "threats")
data class ThreatEntity(
    @PrimaryKey val id: String,
    val type: String,
    val riskLevel: String,
    val source: String,
    val title: String,
    val description: String,
    val explanation: String,
    val actionTaken: String?,
    val senderInfo: String?,
    val contentPreview: String?,
    val timestamp: Long,
    val isNeutralized: Boolean,
    val userFeedback: String?
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val id: String,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long
)

@Entity(tableName = "breach_results")
data class BreachResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val query: String,
    val isBreached: Boolean,
    val breachCount: Int,
    val breachesJson: String,
    val checkedAt: Long
)

@Entity(tableName = "user_behavior")
data class UserBehaviorEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patternType: String,
    val patternData: String,
    val frequency: Int,
    val lastSeen: Long,
    val isNormal: Boolean
)

@Dao
interface ThreatDao {
    @Query("SELECT * FROM threats ORDER BY timestamp DESC")
    fun getAllThreats(): Flow<List<ThreatEntity>>

    @Query("SELECT * FROM threats ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentThreats(limit: Int = 20): Flow<List<ThreatEntity>>

    @Query("SELECT COUNT(*) FROM threats WHERE isNeutralized = 1")
    fun getBlockedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM threats")
    fun getTotalThreatCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThreat(threat: ThreatEntity)

    @Update
    suspend fun updateThreat(threat: ThreatEntity)

    @Delete
    suspend fun deleteThreat(threat: ThreatEntity)

    @Query("DELETE FROM threats")
    suspend fun deleteAll()
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAll()
}

@Dao
interface BreachResultDao {
    @Query("SELECT * FROM breach_results ORDER BY checkedAt DESC")
    fun getAllResults(): Flow<List<BreachResultEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResult(result: BreachResultEntity)
}

@Dao
interface UserBehaviorDao {
    @Query("SELECT * FROM user_behavior ORDER BY lastSeen DESC")
    fun getAllBehaviors(): Flow<List<UserBehaviorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBehavior(behavior: UserBehaviorEntity)

    @Query("UPDATE user_behavior SET frequency = frequency + 1, lastSeen = :timestamp WHERE patternType = :type AND patternData = :data")
    suspend fun updateFrequency(type: String, data: String, timestamp: Long)
}

@Database(
    entities = [
        ThreatEntity::class,
        ChatMessageEntity::class,
        BreachResultEntity::class,
        UserBehaviorEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CyberGuardianDatabase : RoomDatabase() {
    abstract fun threatDao(): ThreatDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun breachResultDao(): BreachResultDao
    abstract fun userBehaviorDao(): UserBehaviorDao
}
