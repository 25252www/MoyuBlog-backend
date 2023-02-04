package com.moyu.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.code.kaptcha.Producer;
import com.moyu.common.vo.LoginVO;
import com.moyu.common.vo.RegisterVO;
import com.moyu.common.lang.Result;
import com.moyu.DO.User;
import com.moyu.service.user.UserService;
import com.moyu.utils.JwtUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;

@Slf4j
@RestController
@Data
@RequestMapping("/user")
@ConfigurationProperties(prefix = "moyusoldier.password")
public class UserController {

    private String key;

    private static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    private

    @Autowired
    UserService userService;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    Producer producer;

    @PostMapping("/login")
    public Result login(@Validated @RequestBody LoginVO loginVO, HttpServletResponse response) {

        User user = userService.getOne(new QueryWrapper<User>().eq("username", loginVO.getUsername()));
        Assert.notNull(user, "用户不存在");
        String pwd = SecureUtil.hmacSha1(key).digestHex(loginVO.getPassword());
        Assert.isTrue(user.getPassword().equals(pwd), "密码不正确");
        String jwt = jwtUtils.generateToken(user.getId());

        response.setHeader("Authorization", jwt);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        return Result.succ(null);
    }

    @RequiresAuthentication
    @PostMapping("/logout")
    public Result logout() {
        SecurityUtils.getSubject().logout();
        return Result.succ(null);
    }

    @GetMapping("/captcha.jpg")
    public void kaptcha(HttpServletResponse response) throws Exception {
        // 输出流
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        // 生成文字验证码
        String text = producer.createText();
        // 把验证码通过shiro存到session
        SecurityUtils.getSubject().getSession().setAttribute(KAPTCHA_SESSION_KEY, text);
        BufferedImage image = producer.createImage(text);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(image, "jpg", out);
        log.info("验证码: " + text);
        out.flush();
    }

    @PostMapping("/register")
    public Result register(@Validated @RequestBody RegisterVO registerVO, HttpServletResponse response) {
        // 一部分校验交给RegisterVO上的注解，一部分校验交给这里
        Assert.isTrue(registerVO.getPassword().equals(registerVO.getConfirmPassword()), "两次输入密码不一致!");
        String kaptcha = (String) SecurityUtils.getSubject().getSession().getAttribute(KAPTCHA_SESSION_KEY);
        Assert.isTrue(registerVO.getCode().equalsIgnoreCase(kaptcha), "验证码不正确");
        Assert.isNull(userService.getOne(new QueryWrapper<User>().eq("username", registerVO.getUsername())), "用户名已被注册");
        // 写入数据库
        User user = new User();
        user.setUsername(registerVO.getUsername());
        user.setPassword(SecureUtil.hmacSha1(key).digestHex(registerVO.getPassword()));
        user.setRole("visitor");
        user.setPhone(registerVO.getPhone());
        boolean bool = userService.save(user);
        Assert.isTrue(bool, "注册失败");
        // 生成jwt
        // 从数据库获得userid
        Integer userid = userService.getOne(new QueryWrapper<User>().eq("username", registerVO.getUsername())).getId();
        String jwt = jwtUtils.generateToken(userid);
        response.setHeader("Authorization", jwt);
        response.setHeader("Access-Control-Expose-Headers", "Authorization");

        return Result.succ(null);
    }

    @GetMapping("/info")
    public Result info(@RequestParam String token) {
        String userId = jwtUtils.getClaimByToken(token).getSubject();
        User user = userService.getById(userId);
        if (user == null) {
            throw new UnknownAccountException("账户不存在");
        }
        // 目前只有角色权限（admin/visitor），后续可扩展
        String[] roles = new String[]{user.getRole()};
        return Result.succ(MapUtil.builder()
                .put("id", user.getId())
                .put("username", user.getUsername())
                .put("roles", roles)
                .put("avatar", user.getAvatar())
                .put("phone", user.getPhone())
                .map()
        );
    }
}
