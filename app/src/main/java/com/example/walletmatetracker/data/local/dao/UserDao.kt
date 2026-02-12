package com.example.walletmatetracker.data.local.dao



import androidx.room.*
import com.example.walletmatetracker.data.local.entity.UserEntity

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("UPDATE users SET isLoggedIn = 0")
    suspend fun clearLoginState()

    @Query("UPDATE users SET isLoggedIn = 1 WHERE id = :userId")
    suspend fun setUserLoggedIn(userId: Int)

    @Query("SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1")
    suspend fun getLoggedInUser(): UserEntity?
}
