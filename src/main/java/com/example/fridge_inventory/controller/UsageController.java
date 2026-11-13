package com.example.fridge_inventory.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.fridge_inventory.entity.Usage;
import com.example.fridge_inventory.form.UsageForm;
import com.example.fridge_inventory.service.StockService;
import com.example.fridge_inventory.service.UsageRejectedException;
import com.example.fridge_inventory.service.UsageService;

import jakarta.validation.Valid;

/**
 * 「使った」機能の窓口。
 *   GET  /usages/new         … 登録画面（在庫からプルダウンで選ぶ）
 *   POST /usages             … 登録処理（在庫の確認は UsageService に任せる）
 *   GET  /usages/{id}        … 詳細画面（ここから削除できる）
 *   POST /usages/{id}/delete … 削除処理
 */
@Controller
@RequestMapping("/usages")
public class UsageController {

    private final UsageService usageService;
    private final StockService stockService;

    public UsageController(UsageService usageService, StockService stockService) {
        this.usageService = usageService;
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
        if (!bindingResult.hasErrors()) {
            try {
                usageService.register(usageForm);
                return "redirect:/stocks";
            } catch (UsageRejectedException e) {
                bindingResult.rejectValue(e.getField(), e.getCode(), e.getMessage());
            }
        }
        model.addAttribute("stocks", stockService.findAllStocks());
        return "usages/form";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usage> usage = usageService.findById(id);
        if (usage.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "その記録は見つかりません");
            return "redirect:/records";
        }
        model.addAttribute("usage", usage.get());
        model.addAttribute("unit", usageService.unitOf(usage.get().getName()));
        return "usages/detail";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        usageService.delete(id);
        redirectAttributes.addFlashAttribute("message", "削除しました");
        return "redirect:/records";
    }
}
