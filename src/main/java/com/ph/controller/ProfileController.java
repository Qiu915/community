package com.ph.controller;

import com.ph.model.User;
import com.ph.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class ProfileController {


    @Autowired
    private QuestionService questionService;

    @GetMapping("/profile/{action}")
    public String profile(@PathVariable(name = "action")String action,
                          Model model,
                          HttpServletRequest request,
                          @RequestParam(value = "pageNo",defaultValue = "1")Integer pageNo,
                          @RequestParam(value = "pageSize",defaultValue = "5")Integer pageSize){
        //进行session登陆状态验证
        User user =(User) request.getSession().getAttribute("user");

        if(user==null){
            return "redirect:/";
        }

        if("questions".equals(action)){
            model.addAttribute("section","questions");
            model.addAttribute("sectionName","我的提问");
        }else if("replies".equals(action)){
            model.addAttribute("section","replies");
            model.addAttribute("sectionName","最新回复");
        }
        Map<String, Object> map = questionService.listByUserId(user.getId(), pageNo, pageSize);
        model.addAttribute("map",map);
        return "profile";
    }
}
