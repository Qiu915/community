package com.ph.controller;

import com.ph.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

@Controller
public class IndexController {

    @Autowired
    private QuestionService questionService;
    @GetMapping("/")
    public String index(HttpServletRequest request,
                        Model model,
                        @RequestParam(value = "pageNo",defaultValue = "1")Integer pageNo,
                        @RequestParam(value = "pageSize",defaultValue = "5")Integer pageSize){

        Map<String, Object> map = questionService.list(pageNo, pageSize);

        model.addAttribute("map",map);
        return "index";
    }
}
