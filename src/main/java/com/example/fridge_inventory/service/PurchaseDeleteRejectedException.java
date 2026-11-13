package com.example.fridge_inventory.service;

/**
 * 「買った」記録の削除を断るときに投げる例外。
 */
public class PurchaseDeleteRejectedException extends RuntimeException {

    public PurchaseDeleteRejectedException(String message) {
        super(message);
    }
}
