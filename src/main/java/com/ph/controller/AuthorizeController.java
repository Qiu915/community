package com.ph.controller;

import com.ph.dto.AccessTokenDTO;
import com.ph.dto.GithubUser;
import com.ph.model.User;
import com.ph.mapper.UserMapper;
import com.ph.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class AuthorizeController {
    @Autowired
    private GithubProvider githubProvider;
    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String clientSecret;
    @Value("${github.redirect.uri}")
    private String redirectUri;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(@RequestParam(name ="code") String code,
                           @RequestParam(name="state") String state,
                           HttpServletRequest request,
                           HttpServletResponse response){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setClient_secret(clientSecret);
        accessTokenDTO.setRedirect_uri(redirectUri);
        accessTokenDTO.setState(state);
        String accessToken = githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser = githubProvider.getGithubUser(accessToken);
       if(null != githubUser && githubUser.getId()!=null){
           User user = userMapper.findByAccountId(githubUser.getId());
           if(null !=user){
               String token = user.getToken();
               response.addCookie(new Cookie("token",token));
               request.getSession().setAttribute("user",githubUser);
           }else{
               User newUser = new User();
               String token = UUID.randomUUID().toString();
               newUser.setToken(token);
               newUser.setAccountId(githubUser.getId());
               newUser.setName(githubUser.getName());
               newUser.setGmtCreate(System.currentTimeMillis());
               newUser.setGmtModified(newUser.getGmtCreate());
               newUser.setAvatarUrl(githubUser.getAvatarUrl());
               userMapper.insert(newUser);
               //登录成功，写cookie和session
               response.addCookie(new Cookie("token",token));
               request.getSession().setAttribute("user",githubUser);
           }
           return "redirect:/";
       }else {
           return "redirect:/";
       }
    }
}
