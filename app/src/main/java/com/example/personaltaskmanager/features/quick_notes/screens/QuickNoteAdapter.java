package com.example.personaltaskmanager.features.quick_notes.screens;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.personaltaskmanager.R;
import com.example.personaltaskmanager.features.quick_notes.data.model.QuickNote;

import java.util.ArrayList;
import java.util.List;

public class QuickNoteAdapter extends RecyclerView.Adapter<QuickNoteAdapter.NoteVH> {

    private List<QuickNote> list = new ArrayList<>();

    public interface OnDeleteListener {
        void onDelete(QuickNote note);
    }

    private final OnDeleteListener deleteListener;

    public QuickNoteAdapter(OnDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public void setData(List<QuickNote> data) {
        this.list = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quick_note, parent, false);
        return new NoteVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteVH holder, int position) {
        QuickNote note = list.get(position);
        holder.text.setText(note.content);

        holder.btnDelete.setOnClickListener(v -> {
            if (deleteListener != null) deleteListener.onDelete(note);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class NoteVH extends RecyclerView.ViewHolder {
        TextView text;
        ImageButton btnDelete;

        public NoteVH(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.tv_note);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
