package com.cyberguardian.di

import android.content.Context
import androidx.room.Room
import com.cyberguardian.data.local.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CyberGuardianDatabase {
        return Room.databaseBuilder(
            context,
            CyberGuardianDatabase::class.java,
            "cyber_guardian_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides fun provideThreatDao(db: CyberGuardianDatabase): ThreatDao = db.threatDao()
    @Provides fun provideChatMessageDao(db: CyberGuardianDatabase): ChatMessageDao = db.chatMessageDao()
    @Provides fun provideBreachResultDao(db: CyberGuardianDatabase): BreachResultDao = db.breachResultDao()
    @Provides fun provideUserBehaviorDao(db: CyberGuardianDatabase): UserBehaviorDao = db.userBehaviorDao()
}
