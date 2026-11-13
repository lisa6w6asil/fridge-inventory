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

import com.example.fridge_inventory.entity.Purchase;
import com.example.fridge_inventory.entity.StorageType;
import com.example.fridge_inventory.form.PurchaseForm;
import com.example.fridge_inventory.service.PurchaseDeleteRejectedException;
import com.example.fridge_inventory.service.PurchaseService;
import com.example.fridge_inventory.service.PurchaseUpdateRejectedException;
import com.example.fridge_inventory.service.UnitMismatchException;

import jakarta.validation.Valid;

/**
 * 「買った」機能の窓口。
 *   GET  /purchases             … 記録の一覧
 *   GET  /purchases/new         … 登録画面
 *   POST /purchases             … 登録処理
 *   GET  /purchases/{id}        … 詳細画面（ここから編集・削除できる）
 *   GET  /purchases/{id}/edit   … 編集画面
 *   POST /purchases/{id}        … 編集処理（在庫の確認は PurchaseService に任せる）
 *   POST /purchases/{id}/delete … 削除処理（在庫の確認は PurchaseService に任せる）
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
        // 一覧は「記録の編集」（/records）にまとめたので、そちらへ転送する
        return "redirect:/records";
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
        try {
            purchaseService.register(purchaseForm);
        } catch (UnitMismatchException e) {
            bindingResult.rejectValue("unit", "mismatch", e.getMessage());
            model.addAttribute("storageTypes", StorageType.values());
            return "purchases/form";
        }
        return "redirect:/records";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Purchase> purchase = purchaseService.findById(id);
        if (purchase.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "その記録は見つかりません");
            return "redirect:/records";
        }
        model.addAttribute("purchase", purchase.get());
        return "purchases/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Purchase> purchase = purchaseService.findById(id);
        if (purchase.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "その記録は見つかりません");
            return "redirect:/records";
        }
        model.addAttribute("purchaseForm", PurchaseForm.from(purchase.get()));
        model.addAttribute("purchaseId", id);
        model.addAttribute("storageTypes", StorageType.values());
        return "purchases/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute PurchaseForm purchaseForm,
                         BindingResult bindingResult,
                         Model model,
                         RedirectAttributes redirectAttributes) {
        if (!bindingResult.hasErrors()) {
            try {
                purchaseService.update(id, purchaseForm);
                redirectAttributes.addFlashAttribute("message", "変更しました");
                return "redirect:/purchases/" + id;
            } catch (PurchaseUpdateRejectedException e) {
                // 特定の入力欄ではなく、画面全体へのエラーとして出す
                bindingResult.reject("rejected", e.getMessage());
            } catch (UnitMismatchException e) {
                bindingResult.rejectValue("unit", "mismatch", e.getMessage());
            }
        }
        model.addAttribute("purchaseId", id);
        model.addAttribute("storageTypes", StorageType.values());
        return "purchases/form";
    }

    // HTMLのformはGETとPOSTしか送れないので、削除もPOSTで受ける
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            purchaseService.delete(id);
            redirectAttributes.addFlashAttribute("message", "削除しました");
            return "redirect:/records";
        } catch (PurchaseDeleteRejectedException e) {
            // 断られたときは、詳細画面に戻して理由を見せる
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/purchases/" + id;
        }
    }
}
