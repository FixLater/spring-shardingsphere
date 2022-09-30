package com.hyk.sharding.mapper;

import com.hyk.sharding.entity.User;

import java.util.List;

public interface UserMapper {
    Boolean save(User u);
    
    List<User> findAll();

    List<User> findAllBySchoolIdList(List<String> schoolIds);
      
    List<User> findByUserIds(List<String> userIds);
    
    Integer countUser(List<String> ids);
}
