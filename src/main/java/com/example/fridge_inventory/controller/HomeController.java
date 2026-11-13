package com.example.fridge_inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * トップ（/）へのアクセスを在庫確認画面（/stocks）へ転送する。
 * メニューは左サイドバーにあるため、専用のトップ画面は持たない。
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/stocks";
    }
}