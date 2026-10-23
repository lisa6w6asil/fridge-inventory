package com.example.fridge_inventory.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.fridge_inventory.entity.Purchase;

/**
 * purchasesテーブルを操作する窓口。
 * 中身を書かなくても、保存・全件取得・ID検索・削除がSpringによって用意される。
 */
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}