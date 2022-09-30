package com.hyk.sharding.service;

import com.hyk.sharding.entity.User;

import java.util.List;

public interface UserService {

    Boolean save(User u);
    
    List<User> findAll();
//
//    List<User> findAllBySchoolIdList(List<String> ids);
//
//    List<User> findByUserIds(List<String> ids);
//
//    Integer countUser(List<String> ids);
}
