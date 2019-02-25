package com.lsd.test.dynmic.source.dao;

import com.lsd.test.dynmic.source.model.User;

import java.util.List;

public interface UserDao {

    List<User> list(Integer tenantId);

    void save(User user);

    void update(User user);
}
