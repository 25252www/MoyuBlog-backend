package com.moyu.controller;

import cn.hutool.crypto.SecureUtil;
import com.moyu.common.lang.Result;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/hello")
    public Result hello(){
        return Result.succ("hello world");
    }
}
