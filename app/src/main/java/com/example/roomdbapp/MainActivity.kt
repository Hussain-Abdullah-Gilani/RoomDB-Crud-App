package com.example.roomdbapp
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdbapp.api.RetrofitService
import com.example.roomdbapp.databinding.ActivityMainBinding
import com.example.roomdbapp.db.UserDao
import com.example.roomdbapp.db.UserDatabase
import com.example.roomdbapp.db.UserEntity
import com.example.roomdbapp.db.UserRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var userviewmodel:UserViewModel
    private var adapter: MyRecyclerViewAdapter? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao = UserDatabase.getInstance(application).userDao

        binding.lifecycleOwner = this

        val retrofitService= RetrofitService.getInstance()
        val repository = UserRepository(retrofitService,dao)
        userviewmodel = ViewModelProvider(this, UserViewModelFactory(repository))[UserViewModel::class.java]
        binding.myViewModel = userviewmodel

        userviewmodel.users.observe(this)
        {
            Log.d("TAG", it.toString())

            adapter?.setList(it)
        }
        userviewmodel.errormsg.observe(this)
        {
            Toast.makeText(this,"Yo no go",Toast.LENGTH_SHORT).show()
        }

        userviewmodel.getUsers()





        //getAllApi()
        userviewmodel.message.observe(this, Observer {
            it.getContentIfNotHandled()?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            }
        })
        binding.searchbar.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                        if(p0 != null){
                            Log.d("TAG",p0.toString())
                            searchDatabase(p0.toString())
                        }
                        return true
            }
            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0 != null){
                    Log.d("TAG",p0.toString())
                    searchDatabase(p0.toString())
                }
                return true
            }
        })
     initRecyclerView()
    }
    private fun initRecyclerView() {
        binding.userRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyRecyclerViewAdapter({ selectedItem: UserEntity -> listItemClicked(selectedItem) })
        binding.userRecyclerView.adapter = adapter
        displayuserList()
    }



    private fun displayuserList() {
        userviewmodel.getSavedUser().observe(this, Observer {
            adapter?.setList(it)
            adapter?.notifyDataSetChanged()
        })
    }
    private fun listItemClicked(user: UserEntity) {
        userviewmodel.initUpdateAndDelete(user)
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun searchDatabase(query: String) {
        val searchQuery="%$query%"
        Log.d("TAG",searchQuery)
        userviewmodel.searchDatabase(searchQuery).observe(this) { list ->
            list.let {
                adapter?.setList(it)
                adapter?.notifyDataSetChanged()
            }
        }
    }
    fun getAllApi()
    {

        //userviewmodel.getdata()
//        api=userviewmodel.returndata()
//        userviewmodel.insertUsersApi(api)
    }

}