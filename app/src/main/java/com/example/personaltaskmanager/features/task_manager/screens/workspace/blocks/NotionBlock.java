package com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks;

public class NotionBlock {

    public enum Type {
        PARAGRAPH,
        TODO,
        BULLET,
        DIVIDER,
        FILE   // thêm block file
    }

    public String id;
    public Type type;
    public String text;
    public boolean isChecked;

    // thêm thuộc tính cho file
    public String fileUri;     // content://...
    public String fileName;    // tên file hiển thị

    public NotionBlock(String id, Type type, String text, boolean isChecked) {
        this.id = id;
        this.type = type;
        this.text = text;
        this.isChecked = isChecked;
    }
}
