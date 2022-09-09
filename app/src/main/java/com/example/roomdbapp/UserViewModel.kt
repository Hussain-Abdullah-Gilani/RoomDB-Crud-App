package com.example.roomdbapp
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.lifecycle.*
import com.example.roomdbapp.api.Api
import com.example.roomdbapp.db.UserDao
import com.example.roomdbapp.db.UserEntity
import com.example.roomdbapp.db.UserRepository
import io.reactivex.internal.util.HalfSerializer.onError
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private var isUpdateOrDelete = false
    val users=MutableLiveData<List<UserEntity>>()
    private lateinit var UserToUpdateOrDelete: UserEntity
    val inputName = MutableLiveData<String>()
    val inputEmail = MutableLiveData<String>()
    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()
    private val statusMessage = MutableLiveData<Event<String>>()
    val errormsg = MutableLiveData<String>()
    var handlejob: Job? = null
    val loading = MutableLiveData<Boolean>()


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
                createUsers()
                insertUser(UserEntity(0, name, email))
                inputName.value = ""
                inputEmail.value = ""
            }
        }
    }
    fun insertUser(user: UserEntity) = viewModelScope.launch {
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
        repository.getAllDbUser().collect {
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

//    fun getdata()
//    {
//        val retro = Retrofit.Builder()
//            .baseUrl("https://gorest.co.in/public/v2/")
//            .addConverterFactory(MoshiConverterFactory.create())
//            .build()
//        val service = retro.create(Api::class.java)
//        val countryRequest = service.listUsers()
//
//
//        countryRequest.enqueue(object : Callback<List<UserEntity>> {
//            override fun onResponse(call: Call<List<UserEntity>>, response: Response<List<UserEntity>>) {
//                if (response.code() == 200) {
//                    Log.d("TAG",response.body().toString())
//                   user= response.body()!!
//                    insertUsersApi(user)
//                }
//            }
//            override fun onFailure(call: Call<List<UserEntity>>, t: Throwable) {
//                Log.d("TAG", "on FAILURE!!!!")
//            }
//        })
//    }


    fun getUsers() {
        handlejob= CoroutineScope(Dispatchers.IO).launch {
            Log.d("TAG","USERVIEWMODEL GETUSERS()")
            val response = repository.getAPIUsers()
            withContext(Dispatchers.Main)
            {
                if (response != null) {
                    if(response.isSuccessful) {
                        Log.d("TAG",response.body().toString())
                        viewModelScope.launch(Dispatchers.IO) {
                            repository.InsertAllApiUserintoDB(response.body())
                            users.postValue(response.body())
                        }
                    }
                }

            }

        }
    }

    fun createUsers() {
        handlejob= CoroutineScope(Dispatchers.IO).launch {
            Log.d("TAG","CreateUSerFunction")
            val user=UserEntity(0,inputName.value.toString(),inputEmail.value.toString())
            val response = repository.insertAPIUser(user)
            withContext(Dispatchers.Main)
            {
                if (response != null) {
                    if (response.isSuccessful) {
                        Log.d("TAG", "User Made Succefully")

                    }
                }
                Log.d("TAG", "User Made Succefully")
            }
        }
    }











//    fun returndata(): List<UserEntity> {
//        return user
//    }

}