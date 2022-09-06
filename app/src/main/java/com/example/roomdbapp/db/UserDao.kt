package com.example.roomdbapp.db
import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow
@Dao
interface UserDao {
    @Insert
    suspend fun insert(user: UserEntity) : Long
    @Update
    suspend fun update(user: UserEntity) : Int
    @Query("DELETE FROM user_table WHERE user_name= :username")
    suspend fun delete(username:String) : Int
    @Query("DELETE FROM user_table")
    suspend fun deleteAll() : Int
    @Query("SELECT * FROM user_table")
    fun getAll():Flow<List<UserEntity>>
    @Query("SELECT * FROM user_table WHERE user_name LIKE :searchQuery")
    fun searchDatabase(searchQuery: String): Flow<List<UserEntity>>
}