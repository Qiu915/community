package com.ph.controller;

import com.ph.dto.QuestionDTO;
import com.ph.model.User;
import com.ph.mapper.QuestionMapper;
import com.ph.model.Question;
import com.ph.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {


    @Autowired
    private QuestionService questionService;


    //接收get请求
    @GetMapping("/publish/{id}")
    public String edit(@PathVariable(name = "id")Long id,
                       Model model){
        QuestionDTO question = questionService.getById(id);
        model.addAttribute("title",question.getTitle());
        model.addAttribute("description",question.getDescription());
        model.addAttribute("tag",question.getTag());
        model.addAttribute("id",id);
        return "publish";
    }

    //接收get请求
    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    //接收post请求
    @PostMapping("/publish")
    public String doPublish(
       @RequestParam(value="title",required = false) String title,
       @RequestParam(value = "description",required = false) String description,
       @RequestParam(value = "tag",required = false) String tag,
       @RequestParam(value = "id",required = false) Long id,
       HttpServletRequest request,
       Model model){

        model.addAttribute("title",title);
        model.addAttribute("description",description);
        model.addAttribute("tag",tag);

        if(title==null || title ==""){
            model.addAttribute("error","标题不能为空");
            return "publish";
        }
        if(description==null || description ==""){
            model.addAttribute("error","问题描述不能为空");
            return "publish";
        }
        if(tag==null || tag ==""){
            model.addAttribute("error","标签不能为空");
            return "publish";
        }

        User user =(User) request.getSession().getAttribute("user");
        if(user==null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }
        Question question = new Question();
        question.setId(id);
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setCreator(user.getId());

        questionService.createOrUpdate(question);
        //questionMapper.create(question);
        //发布完成返回首页
        return "redirect:/";
    }

}
