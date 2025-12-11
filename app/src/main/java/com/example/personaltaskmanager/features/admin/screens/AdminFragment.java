package com.example.personaltaskmanager.features.admin.screens;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.admin.data.AdminRepository;
import com.example.personaltaskmanager.features.admin.screens.adapter.AdminUserAdapter;

public class AdminFragment extends Fragment {

    public AdminFragment() {
        super(R.layout.feature_admin_user_list);
    }

    @Override
    public void onViewCreated(android.view.View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rv = view.findViewById(R.id.rv_users);
        rv.setLayoutManager(new LinearLayoutManager(requireContext()));

        AdminRepository repo = new AdminRepository(requireContext());
        AdminUserAdapter adapter = new AdminUserAdapter(repo.getAllUsers());

        rv.setAdapter(adapter);
    }
}
