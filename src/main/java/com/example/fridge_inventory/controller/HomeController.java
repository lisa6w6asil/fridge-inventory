package com.example.fridge_inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * トップ画面（買った・使った・在庫確認のメニュー）。
 */
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/stocks";
    }
}
