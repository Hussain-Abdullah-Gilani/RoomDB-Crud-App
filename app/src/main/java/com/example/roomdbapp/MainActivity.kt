package com.example.roomdbapp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.roomdbapp.databinding.ActivityMainBinding
import com.example.roomdbapp.db.UserDatabase
import com.example.roomdbapp.db.UserEntity
import com.example.roomdbapp.db.UserRepository

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var userviewmodel:UserViewModel
    private lateinit var adapter: MyRecyclerViewAdapter
    private lateinit var searchbar: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao = UserDatabase.getInstance(application).userDao
        val repository = UserRepository(dao)
        val factory = UserViewModelFactory(repository)
        userviewmodel = ViewModelProvider(this, factory).get(UserViewModel::class.java)
        binding.myViewModel = userviewmodel
        binding.lifecycleOwner = this

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
        binding.subscriberRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyRecyclerViewAdapter({ selectedItem: UserEntity -> listItemClicked(selectedItem) })
        binding.subscriberRecyclerView.adapter = adapter
        displayuserList()
    }

    private fun displayuserList() {
        userviewmodel.getSavedUser().observe(this, Observer {
            adapter.setList(it)
            adapter.notifyDataSetChanged()
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

                adapter.setList(it)
                adapter.notifyDataSetChanged()
            }
        }
    }




}