package com.moyu.shiro;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

// 将数据区信息脱敏后交给Shiro，即不包含私密信息
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountProfile implements Serializable {
    private Integer id;
    private String username;
    private String role;
    private String avatar;
    private String phone;
}
