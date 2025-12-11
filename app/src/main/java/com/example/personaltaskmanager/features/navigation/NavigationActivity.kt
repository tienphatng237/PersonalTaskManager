package com.example.personaltaskmanager.features.navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.personaltaskmanager.R
import com.example.personaltaskmanager.features.admin.screens.AdminFragment
import com.example.personaltaskmanager.features.calendar_events.screens.CalendarFragment
import com.example.personaltaskmanager.features.navigation.SettingsFragment
import com.example.personaltaskmanager.features.task_manager.screens.TaskListFragment
import com.example.personaltaskmanager.features.navigation.HabitFragment

class NavigationActivity : AppCompatActivity() {

    private var role: String = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.navigation_activity)

        role = intent.getStringExtra("role") ?: "user"

        val composeNav = findViewById<ComposeView>(R.id.bottom_nav_compose)

        composeNav.setContent {

            var current by remember {
                mutableStateOf(
                    if (role == "admin") AdminNavItem.MANAGE else NavItem.TASKS
                )
            }

            if (role == "admin") {
                AdminBottomNavBar(
                    current = current as AdminNavItem,
                    onSelect = { selected ->
                        current = selected
                        navigateAdminTo(selected)
                    }
                )
            } else {
                BottomNavBar(
                    current = current as NavItem,
                    onSelect = { selected ->
                        current = selected
                        navigateTo(selected)
                    }
                )
            }
        }

        if (role == "admin") {
            navigateAdminTo(AdminNavItem.MANAGE)
        } else {
            navigateTo(NavItem.TASKS)
        }
    }

    // --------------------- USER NAV -------------------------
    private fun navigateTo(item: NavItem) {
        val fragment: Fragment = when (item) {
            NavItem.TASKS -> TaskListFragment()
            NavItem.CALENDAR -> CalendarFragment()
            NavItem.HABIT -> HabitFragment()
            NavItem.SETTINGS -> SettingsFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_container, fragment)
            .commit()
    }

    // --------------------- ADMIN NAV ------------------------
    private fun navigateAdminTo(item: AdminNavItem) {
        val fragment: Fragment = when (item) {
            AdminNavItem.MANAGE -> AdminFragment()
            AdminNavItem.SETTINGS -> SettingsFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_container, fragment)
            .commit()
    }
}
