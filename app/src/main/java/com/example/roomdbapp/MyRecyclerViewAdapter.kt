package com.example.roomdbapp


import android.app.LauncherActivity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdbapp.databinding.ActivityMainBinding
import com.example.roomdbapp.databinding.RecyclerItemBinding
import com.example.roomdbapp.db.UserEntity

class MyRecyclerViewAdapter(private val clickListener: (UserEntity) -> Unit) :
    RecyclerView.Adapter<MyViewHolder>() {
    private val usersList = ArrayList<UserEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RecyclerItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.recycler_item, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(usersList[position], clickListener)
    }

    fun setList(user: List<UserEntity>) {
        usersList.clear()
        usersList.addAll(user)

    }

}

class MyViewHolder(val binding: RecyclerItemBinding ) : RecyclerView.ViewHolder(binding.root) {

    fun bind(user: UserEntity, clickListener: (UserEntity) -> Unit) {
        binding.nameTextView.text = user.name
        binding.emailTextView.text = user.email
        binding.listItemLayout.setOnClickListener {
            clickListener(user)
        }
    }
}