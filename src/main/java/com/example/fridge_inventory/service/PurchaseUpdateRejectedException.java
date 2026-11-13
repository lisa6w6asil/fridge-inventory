package com.example.fridge_inventory.service;

/**
 * 「買った」記録の編集を断るときに投げる例外。
 */
public class PurchaseUpdateRejectedException extends RuntimeException {

    public PurchaseUpdateRejectedException(String message) {
        super(message);
    }
}
