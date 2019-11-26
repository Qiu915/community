package com.ph.mapper;

import com.ph.model.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("insert into user(name,account_id,token,gmt_create,gmt_modified,avatar_url) values(#{name},#{accountId},#{token},#{gmtCreate},#{gmtModified},#{avatarUrl})")
    void insert(User user);
    @Select("select * from user where token =#{token}")
    User findbyToken(@Param("token") String token);

   /* @Select("select * from user where id =#{id}")
    User findbyId(@Param("id") Integer id);*/

    @Select("select * from user where account_id =#{id}")
    User findByAccountId(@Param("id")Long id);
}
