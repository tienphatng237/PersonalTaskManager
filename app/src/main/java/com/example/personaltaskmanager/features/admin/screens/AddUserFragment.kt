package com.example.personaltaskmanager.features.admin.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.personaltaskmanager.R

class AddUserFragment : Fragment() {

    private lateinit var vm: AdminViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.feature_admin_add_user, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        vm = ViewModelProvider(this)[AdminViewModel::class.java]

        val etUsername = view.findViewById<EditText>(R.id.et_new_username)
        val etEmail = view.findViewById<EditText>(R.id.et_new_email)
        val etPassword = view.findViewById<EditText>(R.id.et_new_password)
        val tvResult = view.findViewById<TextView>(R.id.tv_result_add)
        val btnAdd = view.findViewById<Button>(R.id.btn_insert_user)

        btnAdd.setOnClickListener {
            vm.insertUser(
                etUsername.text.toString(),
                etEmail.text.toString(),
                etPassword.text.toString()
            )
        }

        vm.resultText.observe(viewLifecycleOwner) {
            tvResult.text = it
        }
    }
}
