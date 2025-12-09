package com.example.personaltaskmanager.features.task_manager.screens.workspace;

public interface MoveHandler {

    // Gọi khi block di chuyển (swap)
    void onItemMove(int fromPos, int toPos);

    // Gọi khi thả block => để Activity lưu JSON
    void onItemDrop();
}
