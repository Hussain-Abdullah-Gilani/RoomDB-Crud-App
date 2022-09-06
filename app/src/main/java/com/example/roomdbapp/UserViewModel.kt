package com.example.roomdbapp

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.*
import com.example.roomdbapp.db.UserEntity
import com.example.roomdbapp.db.UserRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private var isUpdateOrDelete = false
    private lateinit var UserToUpdateOrDelete: UserEntity
    val inputName = MutableLiveData<String>()
    val inputEmail = MutableLiveData<String>()
    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun initUpdateAndDelete(user: UserEntity) {
        inputName.value = user.name
        inputEmail.value = user.email
        isUpdateOrDelete = true
        UserToUpdateOrDelete = user
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }

    fun saveOrUpdate() {
        Log.d("TAG",inputName.value.toString())
        if (inputName.value == null) {
            statusMessage.value = Event("Please enter user name")
        } else if (inputEmail.value == null) {
            statusMessage.value = Event("Please enter user email")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Please enter a correct email address")
        } else {
            if (isUpdateOrDelete) {
                UserToUpdateOrDelete.name = inputName.value!!
                UserToUpdateOrDelete.email = inputEmail.value!!
                updateUser(UserToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                insertUser(UserEntity(0, name, email))
                inputName.value = ""
                inputEmail.value = ""
            }
        }
    }

    private fun insertUser(user: UserEntity) = viewModelScope.launch {
        val newRowId = repository.insert(user)
        if (newRowId > -1) {
            statusMessage.value = Event("User Inserted Successfully $newRowId")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }


    private fun updateUser(user: UserEntity) = viewModelScope.launch {
        val noOfRows = repository.update(user)
        if (noOfRows > 0) {
            inputName.value = ""
            inputEmail.value = ""
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Save"
            clearAllOrDeleteButtonText.value = "Clear All"
            statusMessage.value = Event("$noOfRows Row Updated Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun getSavedUser() = liveData {
        repository.users.collect {
            emit(it)
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            deleteUser(UserToUpdateOrDelete)
        } else {
            clearAll()
        }
    }

    private fun deleteUser(subscriber: UserEntity) = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(subscriber)
        if (noOfRowsDeleted > 0) {
            inputName.value = ""
            inputEmail.value = ""
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "Save"
            clearAllOrDeleteButtonText.value = "Clear All"
            statusMessage.value = Event("$noOfRowsDeleted Row Deleted Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    private fun clearAll() = viewModelScope.launch {
        val noOfRowsDeleted = repository.deleteAll()
        if (noOfRowsDeleted > 0) {
            statusMessage.value = Event("$noOfRowsDeleted Users Deleted Successfully")
        } else {
            statusMessage.value = Event("Error Occurred")
        }
    }

    fun searchDatabase(searchQuery: String): LiveData<List<UserEntity>> {
        return repository.searchDatabase(searchQuery).asLiveData()
    }

}