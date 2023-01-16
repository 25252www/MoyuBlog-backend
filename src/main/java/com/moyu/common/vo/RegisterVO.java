package com.moyu.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterVO implements Serializable {
    @NotBlank(message = "用户名不能为空")
    // 用户名限制：小于16个字符，包含文字、字母、数字、下划线和减号
    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9-]{1,16}$", message = "用户名限制：小于16个字符，包含文字、字母、数字、下划线和减号")
    String username;

    @NotBlank(message = "密码不能为空")
    // 密码限制：大于6个字符，应为字母和数字的组合
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$", message = "密码限制：大于6个字符，应为字母和数字的组合")
    String password;

    @NotBlank(message = "确认密码不能为空")
    String repassword;

    // 可以为null，不能为""或者" "
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "手机号格式有误")
    String phone;

    @NotBlank(message = "验证码不能为空")
    String code;
}
