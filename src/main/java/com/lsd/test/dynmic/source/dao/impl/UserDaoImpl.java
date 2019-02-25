package com.lsd.test.dynmic.source.dao.impl;

import com.lsd.test.dynmic.source.dao.UserDao;
import com.lsd.test.dynmic.source.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDaoImpl extends DaoImpl implements UserDao {

    @Override
    public List<User> list(Integer tenantId) {

        List list = null;
        try {
            list = currentSession().createCriteria(User.class, "u").list();
            Thread.sleep(10000);
        }catch (Exception e){

            throw new RuntimeException(e);
        }

       return list ;

    }

    @Override
    public void save(User user) {
        currentSession().save(user);
        System.out.println(1);
    }

    @Override
    public void update(User user) {
        currentSession().update(user);
    }
}
