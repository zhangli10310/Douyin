package com.zl.core.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.zl.core.MainApp
import com.zl.core.db.user.User
import com.zl.core.db.user.UserDao

/**
 *
 *<p></p>
 *
 * Created by zhangli on 2018/2/5 19:34.<br/>
 */
@Database(entities = arrayOf(User::class), version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {

        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase().also { INSTANCE = it }
                }

        private fun buildDatabase() =
                Room.databaseBuilder(MainApp.instance,
                        AppDatabase::class.java, "douyin.db")
                        .build()
    }

}