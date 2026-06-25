package com.sam.myapplication.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Employee::class, AttendanceRecord::class, AttendanceRequest::class, Announcement::class, DailyTimeRecord::class, ChatMessage::class, DailySummaryNote::class, WorkPermit::class, AttritionRecord::class, DARecord::class, ActivityLog::class, EmployeeSchedule::class, ShiftTemplate::class], version = 44, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun employeeDao(): EmployeeDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun workPermitDao(): WorkPermitDao
    abstract fun attritionDao(): AttritionDao
    abstract fun daDao(): DADao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "employee_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
