package com.example.fridge_inventory.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.fridge_inventory.service.RecordService;

/**
 * 「記録の編集」画面の窓口。
 *   GET /records … 買った記録と使った記録を、日付の新しい順に混ぜて並べる
 */
@Controller
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/records")
    public String list(Model model) {
        model.addAttribute("records", recordService.findAllNewestFirst());
        return "records/list";
    }
}
