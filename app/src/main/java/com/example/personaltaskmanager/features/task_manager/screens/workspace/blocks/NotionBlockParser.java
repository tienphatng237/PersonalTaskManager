package com.example.personaltaskmanager.features.task_manager.screens.workspace.blocks;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NotionBlockParser {

    public static List<NotionBlock> fromJson(String json) {
        List<NotionBlock> list = new ArrayList<>();

        if (json == null || json.isEmpty()) return list;

        try {
            JSONArray arr = new JSONArray(json);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);

                NotionBlock block = new NotionBlock(
                        o.getString("id"),
                        NotionBlock.Type.valueOf(o.getString("type")),
                        o.optString("text", ""),
                        o.optBoolean("isChecked", false)
                );

                // ⭐ THÊM 2 FIELD FILE ⭐
                block.fileUri = o.optString("fileUri", null);
                block.fileName = o.optString("fileName", null);

                list.add(block);
            }

        } catch (Exception ignored) {}

        return list;
    }

    public static String toJson(List<NotionBlock> blocks) {
        JSONArray arr = new JSONArray();

        try {
            for (NotionBlock b : blocks) {

                JSONObject o = new JSONObject();
                o.put("id", b.id);
                o.put("type", b.type.name());
                o.put("text", b.text);
                o.put("isChecked", b.isChecked);

                // ⭐ THÊM 2 FIELD FILE ⭐
                o.put("fileUri", b.fileUri == null ? JSONObject.NULL : b.fileUri);
                o.put("fileName", b.fileName == null ? JSONObject.NULL : b.fileName);

                arr.put(o);
            }
        } catch (Exception ignored) {}

        return arr.toString();
    }
}
