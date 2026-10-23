package com.example.fridge_inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.fridge_inventory.dto.UsageTotal;
import com.example.fridge_inventory.entity.Usage;

/**
 * usagesテーブルを操作する窓口。
 */
public interface UsageRepository extends JpaRepository<Usage, Long> {

    /**
     * 品目名＋期限＋保存方法 ごとに、使った数量を合計する（GROUP BY）。
     */
    @Query("""
            select new com.example.fridge_inventory.dto.UsageTotal(
                u.name, u.expiryDate, u.storageType, sum(u.quantity))
            from Usage u
            group by u.name, u.expiryDate, u.storageType
            """)
    List<UsageTotal> sumByGroup();
}
