package com.example.fridge_inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.fridge_inventory.entity.StorageType;
import com.example.fridge_inventory.service.StockService;

/**
 * 在庫確認画面（冷蔵・冷凍・常温ごとに、期限の近い順）。
 */
@Controller
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/stocks")
    public String list(Model model) {
        model.addAttribute("stocks", stockService.findAllStocks());
        model.addAttribute("storageTypes", StorageType.values());
        return "stocks/list";
    }
}
