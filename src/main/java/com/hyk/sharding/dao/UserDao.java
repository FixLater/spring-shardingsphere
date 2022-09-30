package com.hyk.sharding.dao;

import com.hyk.sharding.entity.User;
import com.hyk.sharding.hibernate.HibernateDao;
import org.springframework.stereotype.Repository;


/**
 * App 的下载配置
 */
@Repository
public class UserDao extends HibernateDao<User, String> {
}
