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

class AdminFragment : Fragment() {

    private lateinit var vm: AdminViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.feature_admin_user_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm = ViewModelProvider(this)[AdminViewModel::class.java]

        // INPUT FIELD
        val etId = view.findViewById<EditText>(R.id.et_user_id)
        val tvResult = view.findViewById<TextView>(R.id.tv_result)

        // BUTTONS
        val btnGetAll = view.findViewById<Button>(R.id.btn_get_all)
        val btnGetById = view.findViewById<Button>(R.id.btn_get_user_id)
        val btnGotoAdd = view.findViewById<Button>(R.id.btn_goto_add_user)

        // GET ALL USERS
        btnGetAll.setOnClickListener {
            vm.getAllUsers()
        }

        // GET USER BY ID
        btnGetById.setOnClickListener {
            val id = etId.text.toString().toIntOrNull()
            if (id != null) vm.getUserById(id)
            else tvResult.text = "ID không hợp lệ!"
        }

        // OPEN ADD USER SCREEN
        btnGotoAdd.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.nav_container, AddUserFragment())
                .addToBackStack(null)
                .commit()
        }

        // OBSERVE RESULT
        vm.resultText.observe(viewLifecycleOwner) { msg ->
            tvResult.text = msg
        }
    }
}
