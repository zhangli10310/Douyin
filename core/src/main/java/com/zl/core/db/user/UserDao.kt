package com.zl.core.db.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

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