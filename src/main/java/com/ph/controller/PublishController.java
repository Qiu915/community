package com.ph.controller;

import com.ph.dto.User;
import com.ph.mapper.QuestionMapper;
import com.ph.mapper.UserMapper;
import com.ph.model.Question;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Controller
public class PublishController {

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    //接收get请求
    @GetMapping("/publish")
    public String publish(){
        return "publish";
    }

    //接收post请求
    @PostMapping("/publish")
    public String dopublish(
       @RequestParam(value="title",required = false) String title,
       @RequestParam(value = "description",required = false) String description,
       @RequestParam(value = "tag",required = false) String tag,
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

        User user=null;
        Cookie[] cookies = request.getCookies();
        if(cookies!=null && cookies.length!=0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    String token = cookie.getValue();
                    user = userMapper.findbyToken(token);
                    if (user != null) {
                        request.getSession().setAttribute("user", user);
                    }
                    break;
                }
            }
        }

        if(user==null){
            model.addAttribute("error","用户未登录");
            return "publish";
        }
        Question question = new Question();
        question.setTitle(title);
        question.setDescription(description);
        question.setTag(tag);
        question.setId(user.getId());
        question.setGmtCreate(System.currentTimeMillis());
        question.setGmtModified(question.getGmtCreate());
        questionMapper.create(question);
        //发布完成返回首页
        return "redirect:/";
    }

}
