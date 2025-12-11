package com.example.personaltaskmanager.features.admin.screens.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.authentication.data.local.entity.UserEntity;
import java.util.List;

public class AdminUserAdapter extends RecyclerView.Adapter<AdminUserAdapter.VH> {

    private final List<UserEntity> users;

    public AdminUserAdapter(List<UserEntity> users) {
        this.users = users;
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvRole;

        public VH(View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_username);
            tvEmail = v.findViewById(R.id.tv_email);
            tvRole = v.findViewById(R.id.tv_role);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.feature_admin_user_item, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        UserEntity u = users.get(position);
        h.tvName.setText(u.username);
        h.tvEmail.setText(u.email);
        h.tvRole.setText(u.role);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}
