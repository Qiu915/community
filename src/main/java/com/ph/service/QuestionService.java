package com.ph.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ph.dto.QuestionDTO;
import com.ph.model.User;
import com.ph.mapper.QuestionMapper;
import com.ph.mapper.UserMapper;
import com.ph.model.Question;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QuestionService {
    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserMapper userMapper;

    public Map<String,Object> list(Integer pageNo, Integer pageSize) {
        Map<String,Object> map = new HashMap();

        ArrayList<QuestionDTO> questionDTOS = new ArrayList<>();

        PageHelper.startPage(pageNo,pageSize);
        //后面一个全查询
        List<Question> questions = questionMapper.list();
        PageInfo page = new PageInfo(questions,5);
        for (Question question : questions) {
            User user = userMapper.findByAccountId(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        map.put("page",page);
        map.put("questions",questionDTOS);
        return map;
    }

    public Map<String, Object> listByUserId(Long id, Integer pageNo, Integer pageSize) {
        Map<String,Object> map = new HashMap();
        ArrayList<QuestionDTO> questionDTOS = new ArrayList<>();
        PageHelper.startPage(pageNo,pageSize);
        //后面一个全查询
        List<Question> questions = questionMapper.listByUserId(id);

        PageInfo page = new PageInfo(questions,5);
        for (Question question : questions) {
            User user = userMapper.findByAccountId(question.getCreator());
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(user);
            questionDTOS.add(questionDTO);
        }
        map.put("page",page);
        map.put("questions",questionDTOS);
        return map;
    }
}
