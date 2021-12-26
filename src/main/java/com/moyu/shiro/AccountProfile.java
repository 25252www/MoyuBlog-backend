package com.moyu.shiro;

import java.io.Serializable;

/**
 * 而在AccountRealm我们还用到了AccountProfile，这是为了登录成功之后返回的一个用户信息的载体
 */
public class AccountProfile implements Serializable {
    private Integer id;
    private String username;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "AccountProfile{" +
                "id=" + id +
                ", username='" + username + '\'' +
                '}';
    }
}
