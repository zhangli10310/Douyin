package com.zl.core.db.user

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

/**
 *
 *<p></p>
 *
 */
@Dao
interface UserDao {

    @Query("SELECT * FROM Users WHERE id = :id")
    fun getUserById(id: Int): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Query("DELETE FROM Users")
    fun deleteAllUsers()

}