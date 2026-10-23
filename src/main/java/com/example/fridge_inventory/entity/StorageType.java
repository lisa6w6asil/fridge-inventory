package com.example.fridge_inventory.entity;

/**
 * 保存方法（冷蔵・冷凍・常温）を表す列挙型。
 * 画面に出す日本語は label で持ち、DBには REFRIGERATED などの名前で保存する。
 */
public enum StorageType {

    REFRIGERATED("冷蔵"),
    FROZEN("冷凍"),
    ROOM_TEMPERATURE("常温");

    private final String label;

    StorageType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}