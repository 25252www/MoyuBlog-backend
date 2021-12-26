package com.moyu.controller;

import com.moyu.common.lang.Result;
import com.moyu.pojo.User;
import com.moyu.service.user.UserService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequiresAuthentication
    @GetMapping("/index")
    public Result index() {
        User user = userService.getById(1);
        return Result.succ(user);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable("id") Integer id) {
        return Result.succ(userService.getById(id));
    }


    @PostMapping("/save")
    public Result save(@Validated @RequestBody User user) {
        return Result.succ(user);
    }

}
