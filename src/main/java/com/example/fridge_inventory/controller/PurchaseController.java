package com.example.fridge_inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fridge_inventory.entity.StorageType;
import com.example.fridge_inventory.form.PurchaseForm;
import com.example.fridge_inventory.service.PurchaseService;

import jakarta.validation.Valid;

/**
 * 「買った」機能の窓口。
 *   GET  /purchases      … 買った記録の一覧
 *   GET  /purchases/new  … 登録画面
 *   POST /purchases      … 登録処理（保存は PurchaseService に任せる）
 */
@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("purchases", purchaseService.findAllNewestFirst());
        return "purchases/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("purchaseForm", new PurchaseForm());
        model.addAttribute("storageTypes", StorageType.values());
        return "purchases/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute PurchaseForm purchaseForm,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("storageTypes", StorageType.values());
            return "purchases/form";
        }
        purchaseService.register(purchaseForm);
        return "redirect:/purchases";
    }
}
