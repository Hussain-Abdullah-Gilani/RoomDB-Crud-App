package com.example.roomdbapp.db
import android.util.Log
import com.example.roomdbapp.api.Api
import com.example.roomdbapp.api.RetrofitService
import com.example.roomdbapp.api.RetrofitService.Companion.retrofitService
import kotlinx.coroutines.flow.Flow
import org.w3c.dom.Entity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class UserRepository(private val retro: RetrofitService,private val dao: UserDao)
{

    //API
    suspend fun getAPIUsers(): Response<List<UserEntity>>? {
       return retrofitService?.getUsers()
    }
    suspend fun insertAPIUser(user: UserEntity): Response<List<UserEntity>>? {
        return retrofitService?.createUsers()
    }



    //DATABASE
    suspend fun getAllDbUser(): Flow<List<UserEntity>> {
        return dao.getAll()
    }
    suspend fun InsertAllApiUserintoDB(user: List<UserEntity>?){
        return dao.insertApi(user)
    }


    suspend fun insert(user: UserEntity): Long {
        return dao.insert(user)
    }
    suspend fun update(user: UserEntity): Int {
        return dao.update(user)
    }
    suspend fun delete(user: UserEntity): Int {
        return dao.delete(user.name)
    }
    suspend fun deleteAll(): Int {
        return dao.deleteAll()
    }
    fun searchDatabase(searchQuery: String): Flow<List<UserEntity>> {
        return dao.searchDatabase(searchQuery)
    }
}