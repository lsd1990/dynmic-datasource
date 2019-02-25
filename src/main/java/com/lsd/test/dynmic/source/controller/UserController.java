package com.lsd.test.dynmic.source.controller;

import com.lsd.test.dynmic.source.model.User;
import com.lsd.test.dynmic.source.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("list")
    @ResponseBody
    public List<User> list( @RequestParam("tenantid") Integer tenantid){

        return userService.list(tenantid);

    }

    @PostMapping("save")
    @ResponseBody
    public String save(@RequestBody User user){

        userService.save(user);

        return "success";

    }

    @PostMapping("update")
    @ResponseBody
    public String update(@RequestBody User user){

        userService.update(user);

        return "success";

    }
}
