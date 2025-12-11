package com.example.personaltaskmanager.features.admin.screens.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.admin.data.model.AdminUser

class AdminUserAdapter(
    private var users: List<AdminUser>
) : RecyclerView.Adapter<AdminUserAdapter.UserVH>() {

    inner class UserVH(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tv_username)
        val tvEmail: TextView = view.findViewById(R.id.tv_email)
        val tvRole: TextView = view.findViewById(R.id.tv_role)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.feature_admin_user_item, parent, false)
        return UserVH(view)
    }

    override fun onBindViewHolder(holder: UserVH, position: Int) {
        val u = users[position]
        holder.tvName.text = u.username
        holder.tvEmail.text = u.email
        holder.tvRole.text = u.role
    }

    override fun getItemCount(): Int = users.size

    fun update(newList: List<AdminUser>) {
        users = newList
        notifyDataSetChanged()
    }
}
