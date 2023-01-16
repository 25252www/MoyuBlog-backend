package com.moyu.controller;

import com.moyu.common.lang.Result;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public Result hello(){
        return Result.succ("hello world");
    }

    @RequiresAuthentication
    @GetMapping("/loginhello")
    public Result loginhello(){
        return Result.succ("hello world");
    }

    @RequiresRoles("admin")
    @GetMapping("/adminhello")
    public Result adminhello(){
        return Result.succ("hello world");
    }
}
