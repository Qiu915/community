package com.ph.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ph.dto.QuestionDTO;
import com.ph.exception.CustomiseErrorCode;
import com.ph.exception.CustomiseException;
import com.ph.mapper.QuestionMapperExt;
import com.ph.model.QuestionExample;
import com.ph.model.User;
import com.ph.mapper.QuestionMapper;
import com.ph.mapper.UserMapper;
import com.ph.model.Question;
import com.ph.model.UserExample;
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

    @Autowired
    private QuestionMapperExt questionMapperExt;

    public Map<String,Object> listAll(Integer pageNo, Integer pageSize) {
        Map<String,Object> map = new HashMap();

        ArrayList<QuestionDTO> questionDTOS = new ArrayList<>();

        PageHelper.startPage(pageNo,pageSize);
        //后面一个全查询
        List<Question> questions = questionMapperExt.list();
        PageInfo page = new PageInfo(questions,5);
        for (Question question : questions) {
            UserExample userExample = new UserExample();
            userExample.createCriteria().andIdEqualTo(question.getCreator());
            List<User> users = userMapper.selectByExample(userExample);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(users.get(0));
            questionDTOS.add(questionDTO);
        }
        map.put("page",page);
        map.put("questions",questionDTOS);
        return map;
    }

    public Map<String, Object> listByUserId(Long id, Integer pageNo, Integer pageSize) {
        Map<String,Object> map = new HashMap();
        ArrayList<QuestionDTO> questionDTOS = new ArrayList<>();
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria()
                .andCreatorEqualTo(id);
        PageHelper.startPage(pageNo,pageSize);
        //后面一个全查询
        List<Question> questions = questionMapper.selectByExample(questionExample);

        PageInfo page = new PageInfo(questions,5);
        for (Question question : questions) {
            UserExample userExample = new UserExample();
            userExample.createCriteria().andIdEqualTo(question.getCreator());
            List<User> users = userMapper.selectByExample(userExample);
            QuestionDTO questionDTO = new QuestionDTO();
            BeanUtils.copyProperties(question,questionDTO);
            questionDTO.setUser(users.get(0));
            questionDTOS.add(questionDTO);
        }
        map.put("page",page);
        map.put("questions",questionDTOS);
        return map;
    }

    public QuestionDTO getById(Long id) {
        QuestionDTO questionDTO = new QuestionDTO();
        QuestionExample questionExample = new QuestionExample();
        questionExample.createCriteria().andIdEqualTo(id);
        List<Question> questions = questionMapper.selectByExample(questionExample);
       // Question question = questionMapper.selectByPrimaryKey(id);
        if(questions.size()<1){
            throw new CustomiseException(CustomiseErrorCode.QUESTION_NOT_FOUND);
        }
        Question question = questions.get(0);
        UserExample userExample = new UserExample();
        userExample.createCriteria().andIdEqualTo(question.getCreator());
        List<User> users = userMapper.selectByExample(userExample);
        BeanUtils.copyProperties(question,questionDTO);
        questionDTO.setUser(users.get(0));
        return questionDTO;
    }

    public void createOrUpdate(Question question) {
        if(null==question.getId()){
            //创建
            question.setGmtCreate(System.currentTimeMillis());
            question.setGmtModified(question.getGmtCreate());
            questionMapper.insert(question);
        }else{
            //更新
            Question updateQuestion = new Question();
            updateQuestion.setTitle(question.getTitle());
            updateQuestion.setDescription(question.getDescription());
            updateQuestion.setTag(question.getTag());
            updateQuestion.setGmtModified(System.currentTimeMillis());
            QuestionExample questionExample = new QuestionExample();
            questionExample.createCriteria()
                    .andIdEqualTo(question.getId());
            int update = questionMapper.updateByExampleSelective(updateQuestion, questionExample);
            if(update!=1){
                throw new CustomiseException(CustomiseErrorCode.QUESTION_NOT_FOUND);
            }
        }
    }
}
