package com.example.fridge_inventory.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fridge_inventory.dto.StockItem;
import com.example.fridge_inventory.form.UsageForm;
import com.example.fridge_inventory.repository.UsageRepository;
import com.example.fridge_inventory.service.StockService;

import jakarta.validation.Valid;

/**
 * 「使った」機能。
 *   GET  /usages/new … 登録画面（在庫からプルダウンで選ぶ）
 *   POST /usages     … 登録処理（在庫より多く使おうとしたらエラー）
 */
@Controller
@RequestMapping("/usages")
public class UsageController {

    private final UsageRepository usageRepository;
    private final StockService stockService;

    public UsageController(UsageRepository usageRepository, StockService stockService) {
        this.usageRepository = usageRepository;
        this.stockService = stockService;
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("usageForm", new UsageForm());
        model.addAttribute("stocks", stockService.findAllStocks());
        return "usages/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute UsageForm usageForm,
                         BindingResult bindingResult,
                         Model model) {
        // 入力チェック（空欄・0以下など）で問題がなければ、在庫との突き合わせをする
        if (!bindingResult.hasErrors()) {
            Optional<StockItem> stock = stockService.findStock(usageForm.getStockId());
            if (stock.isEmpty()) {
                bindingResult.rejectValue("stockId", "notFound", "選んだ食材の在庫が見つかりません");
            } else if (usageForm.quantityInHundredths() > stock.get().remaining()) {
                bindingResult.rejectValue("quantity", "exceeds",
                        "在庫（残り " + stock.get().quantityDisplay() + " " + stock.get().unit()
                                + "）より多くは使えません");
            } else {
                usageRepository.save(usageForm.toEntity(stock.get()));
                return "redirect:/stocks";
            }
        }
        model.addAttribute("stocks", stockService.findAllStocks());
        return "usages/form";
    }
}
