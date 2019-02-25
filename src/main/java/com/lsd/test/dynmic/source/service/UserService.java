package com.lsd.test.dynmic.source.service;

import com.lsd.test.dynmic.source.model.User;

import java.util.List;

public interface UserService {

    List list(Integer tenantId);

    void save(User user);

    void update(User user);
}
