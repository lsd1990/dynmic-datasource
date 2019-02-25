package com.lsd.test.dynmic.source.service.impl;


import com.lsd.test.dynmic.source.dao.UserDao;
import com.lsd.test.dynmic.source.model.User;
import com.lsd.test.dynmic.source.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public List list(Integer tenantId) {
        return userDao.list(tenantId);
    }

    @Override
    public void save(User user) {
        userDao.save(user);
        System.out.println(1);
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }
}
