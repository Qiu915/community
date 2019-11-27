package com.ph.controller;

import com.ph.dto.AccessTokenDTO;
import com.ph.dto.GithubUser;
import com.ph.model.User;
import com.ph.mapper.UserMapper;
import com.ph.model.UserExample;
import com.ph.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
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
           UserExample example = new UserExample();
           example.createCriteria()
                   .andAccountIdEqualTo(Math.toIntExact(githubUser.getId()));
           List<User> users = userMapper.selectByExample(example);
           if( users.size()!=0){
               String token = users.get(0).getToken();
               response.addCookie(new Cookie("token",token));
               HttpSession session=request.getSession();
               User user =users.get(0);
               session.setAttribute("user",user);
           }else{
               User newUser = new User();
               String token = UUID.randomUUID().toString();
               newUser.setToken(token);
               newUser.setAccountId(Math.toIntExact(githubUser.getId()));
               newUser.setName(githubUser.getName());
               newUser.setGmtCreate(System.currentTimeMillis());
               newUser.setGmtModified(newUser.getGmtCreate());
               newUser.setAvatarUrl(githubUser.getAvatarUrl());
               userMapper.insert(newUser);
               //登录成功，写cookie和session
               response.addCookie(new Cookie("token",token));
               request.getSession().setAttribute("user",newUser);
           }
           return "redirect:/";
       }else {
           return "redirect:/";
       }
    }

    @GetMapping("logOut")
    public String logOut(HttpServletRequest request,
                         HttpServletResponse response){
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/";
    }
}
