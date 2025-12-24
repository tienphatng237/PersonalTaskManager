package com.example.personaltaskmanager.features.navigation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.authentication.data.repository.AuthRepository // ⭐ THÊM IMPORT AuthRepository
import com.example.personaltaskmanager.features.authentication.domain.usecase.LogoutUseCase // ⭐ THÊM IMPORT LogoutUseCase
import com.example.personaltaskmanager.features.authentication.screens.LoginActivity // ⭐ THÊM IMPORT LoginActivity
import com.example.personaltaskmanager.ai.GeminiApiService
import com.example.personaltaskmanager.features.export.ExportUtils
import com.example.personaltaskmanager.features.habit_tracker.viewmodel.HabitViewModel
import com.example.personaltaskmanager.features.task_manager.viewmodel.TaskViewModel

// DÒNG IMPORT GÂY LỖI ĐÃ BỊ XÓA HOẶC BỊ COMMENT

class SettingsFragment : Fragment() {

    // Khai báo Repository và UseCase
    private lateinit var authRepository: AuthRepository
    private lateinit var logoutUseCase: LogoutUseCase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Khởi tạo Repository và UseCase
        authRepository = AuthRepository(requireContext())
        logoutUseCase = LogoutUseCase(authRepository)
        return inflater.inflate(R.layout.feature_settings_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // THÊM LOGIC: Gán giá trị, Icon và xử lý click cho các mục
        setupSettingItem(view, R.id.setting_profile_container,
            R.drawable.feature_task_manager_ic_image_placeholder, // Dùng icon Placeholder tạm thời
            "Quản lý Hồ sơ",
            "Cập nhật thông tin cá nhân và mật khẩu") {
            val intent = Intent(requireContext(), com.example.personaltaskmanager.features.profile_settings.screens.ProfileSettingsActivity::class.java)
            startActivity(intent)
        }

        setupSettingItem(view, R.id.setting_logout,
            android.R.drawable.ic_menu_close_clear_cancel,
            "Đăng xuất",
            "Thoát khỏi tài khoản hiện tại") {

            // GỌI HÀM LOGOUT VÀ CHUYỂN HƯỚNG
            performLogout()
        }

        // Mục Theme (dùng switch)
        setupSettingItem(view, R.id.setting_theme,
            android.R.drawable.ic_menu_view,
            "Chế độ Sáng/Tối",
            "Tùy chỉnh giao diện ứng dụng") {
            // Logic cho switch sẽ được xử lý riêng nếu cần
        }

        setupSettingItem(view, R.id.setting_language,
            android.R.drawable.ic_menu_compass, // Icon la bàn tạm
            "Ngôn ngữ",
            "Tiếng Việt (Mặc định)") {
            showLanguageDialog()
        }

        setupSettingItem(view, R.id.setting_export,
            android.R.drawable.ic_menu_share,
            "Xuất dữ liệu",
            "Xuất tasks và habits ra file CSV") {
            showExportDialog()
        }
    }

    /**
     * Hàm thực hiện logic Đăng xuất: Gọi UseCase, xóa session và chuyển hướng.
     */
    private fun performLogout() {
        // 1. Thực hiện logic logout (xóa SharedPreferences)
        logoutUseCase.execute()

        // 2. Chuyển hướng đến LoginActivity
        val intent = Intent(requireContext(), LoginActivity::class.java).apply {
            // Xóa tất cả các Activity khỏi stack và bắt đầu Activity mới
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)

        // 3. Kết thúc NavigationActivity chứa Fragment này
        activity?.finish()
    }


    // Hàm tiện ích để gán giá trị runtime cho các mục Settings
    private fun setupSettingItem(
        parentView: View,
        containerId: Int,
        iconRes: Int,
        title: String,
        subtitle: String,
        onClick: () -> Unit
    ) {
        val container = parentView.findViewById<View>(containerId)
        if (container != null) {
            container.findViewById<ImageView>(R.id.iv_icon)?.setImageResource(iconRes)
            container.findViewById<TextView>(R.id.tv_title)?.text = title
            container.findViewById<TextView>(R.id.tv_subtitle)?.text = subtitle
            container.setOnClickListener { onClick() }
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Tiếng Việt", "English", "中文", "日本語")
        val currentLanguage = "Tiếng Việt" // Có thể lấy từ SharedPreferences

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Chọn ngôn ngữ")
            .setSingleChoiceItems(languages, languages.indexOf(currentLanguage)) { dialog, which ->
                // Lưu ngôn ngữ đã chọn vào SharedPreferences
                val prefs = requireContext().getSharedPreferences("app_prefs", android.content.Context.MODE_PRIVATE)
                prefs.edit().putString("language", languages[which]).apply()
                
                android.widget.Toast.makeText(
                    requireContext(),
                    "Đã chọn: ${languages[which]}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
                
                dialog.dismiss()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showExportDialog() {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Xuất dữ liệu")
            .setItems(arrayOf("Xuất Tasks", "Xuất Habits", "Xuất tất cả")) { dialog, which ->
                val taskViewModel = ViewModelProvider(requireActivity())[TaskViewModel::class.java]
                val habitViewModel = ViewModelProvider(requireActivity())[HabitViewModel::class.java]

                when (which) {
                    0 -> { // Export Tasks
                        taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                            if (tasks != null && tasks.isNotEmpty()) {
                                ExportUtils.exportTasksToCSV(requireContext(), tasks)
                            } else {
                                android.widget.Toast.makeText(
                                    requireContext(),
                                    "Không có task nào để xuất",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    1 -> { // Export Habits
                        habitViewModel.getAllHabits().observe(viewLifecycleOwner) { habits ->
                            if (habits != null && habits.isNotEmpty()) {
                                ExportUtils.exportHabitsToCSV(requireContext(), habits)
                            } else {
                                android.widget.Toast.makeText(
                                    requireContext(),
                                    "Không có habit nào để xuất",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                    2 -> { // Export All
                        taskViewModel.allTasks.observe(viewLifecycleOwner) { tasks ->
                            habitViewModel.getAllHabits().observe(viewLifecycleOwner) { habits ->
                                if (tasks != null && tasks.isNotEmpty()) {
                                    ExportUtils.exportTasksToCSV(requireContext(), tasks)
                                }
                                if (habits != null && habits.isNotEmpty()) {
                                    ExportUtils.exportHabitsToCSV(requireContext(), habits)
                                }
                                if ((tasks == null || tasks.isEmpty()) && (habits == null || habits.isEmpty())) {
                                    android.widget.Toast.makeText(
                                        requireContext(),
                                        "Không có dữ liệu để xuất",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

}