package com.example.fridge_inventory.service;

/**
 * 同じ品目名なのに、これまでと違う単位で登録・編集しようとしたときに投げる例外。
 * 単位が混ざると、在庫の合計が「3本 + 500g = 503本」のようにおかしくなるため。
 */
public class UnitMismatchException extends RuntimeException {

    public UnitMismatchException(String message) {
        super(message);
    }
}
