package com.example.fridge_inventory.service;

/**
 * 「使った」の登録を断るときに投げる例外。
 * どの入力欄のエラーとして画面に出すかを field で持つ。
 */
public class UsageRejectedException extends RuntimeException {

    private final String field;
    private final String code;

    public UsageRejectedException(String field, String code, String message) {
        super(message);
        this.field = field;
        this.code = code;
    }

    public String getField() {
        return field;
    }

    public String getCode() {
        return code;
    }
}
