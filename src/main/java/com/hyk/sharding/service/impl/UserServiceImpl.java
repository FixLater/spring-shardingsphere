package com.hyk.sharding.service.impl;

import com.hyk.sharding.entity.User;
import com.hyk.sharding.mapper.UserMapper;
import com.hyk.sharding.service.UserService;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    public UserMapper userMapper;
    
    public Boolean save(User u) {
        return userMapper.save(u);
    }

    public List<User> findAll() {
        return userMapper.findAll();
    }
//
//    @Override
//    public List<User> findAllBySchoolIdList(List<String> schoolIds) {
//        return userMapper.findAllBySchoolIdList(schoolIds);
//    }
//
//    public List<User> findByUserIds(List<String> ids) {
//        return userMapper.findByUserIds(ids);
//    }
//
//    public Integer countUser(List<String> ids) {
//        return userMapper.countUser(ids);
//    }

}
