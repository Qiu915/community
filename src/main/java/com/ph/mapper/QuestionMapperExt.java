package com.ph.mapper;

import com.ph.model.Question;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuestionMapperExt {

    List<Question> list();
}