package com.kai.webby.dao;


import com.kai.webby.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

@Component  //需不需要，再看   标明该类将被扫进IOC容器
@Mapper
public interface UserDAO {

    int addUser(User user);

    User selectById(@Param("id") int id);

    User selectByName(@Param("name") String name);
}
