package com.example.fridge_inventory.controller;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.fridge_inventory.entity.StorageType;
import com.example.fridge_inventory.form.PurchaseForm;
import com.example.fridge_inventory.repository.PurchaseRepository;

import jakarta.validation.Valid;

/**
 * 「買った」機能の画面とURLをつなぐクラス。
 *   GET  /purchases      … 買った記録の一覧
 *   GET  /purchases/new  … 登録画面
 *   POST /purchases      … 登録処理
 */
@Controller
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseRepository purchaseRepository;

    // コンストラクタでRepositoryを受け取る（Springが自動で渡してくれる）
    public PurchaseController(PurchaseRepository purchaseRepository) {
        this.purchaseRepository = purchaseRepository;
    }

    // 一覧（購入日の新しい順）
    @GetMapping
    public String list(Model model) {
        model.addAttribute("purchases",
                purchaseRepository.findAll(Sort.by(Sort.Direction.DESC, "purchasedDate", "id")));
        return "purchases/list";
    }

    // 登録画面を表示
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("purchaseForm", new PurchaseForm());
        model.addAttribute("storageTypes", StorageType.values());
        return "purchases/form";
    }

    // 登録処理。入力エラーがあれば画面に戻す
    @PostMapping
    public String create(@Valid @ModelAttribute PurchaseForm purchaseForm,
                         BindingResult bindingResult,
                         Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("storageTypes", StorageType.values());
            return "purchases/form";
        }
        purchaseRepository.save(purchaseForm.toEntity());
        return "redirect:/purchases";
    }
}