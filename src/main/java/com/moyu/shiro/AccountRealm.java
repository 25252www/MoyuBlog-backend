package com.moyu.shiro;


import cn.hutool.core.bean.BeanUtil;
import com.moyu.DO.User;
import com.moyu.service.user.UserService;
import com.moyu.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;

// Shiro中的DAO层
@Slf4j
public class AccountRealm extends AuthorizingRealm {
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserService userService;

    // 为了让realm支持jwt的凭证校验
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 获取用户权限并返回
        AccountProfile profile = (AccountProfile) principalCollection.getPrimaryPrincipal();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addRole(profile.getRole());
        return simpleAuthorizationInfo;
    }

    // 认证为什么还要访问数据库呢？因为我们的jwt中只有用户id，而我们需要的是用户的所有信息
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) authenticationToken;
        String userId = jwtUtils.getClaimByToken((String) jwtToken.getPrincipal()).getSubject();
        User user = userService.getById(userId);
        if (user == null) {
            throw new UnknownAccountException("账户不存在");
        }
        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);
        return new SimpleAuthenticationInfo(profile, jwtToken.getCredentials(), getName());
    }
}
