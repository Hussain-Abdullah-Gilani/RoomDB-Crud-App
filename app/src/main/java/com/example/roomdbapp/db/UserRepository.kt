package com.example.roomdbapp.db
import kotlinx.coroutines.flow.Flow
class UserRepository(private val dao: UserDao) {
    val users = dao.getAll()
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