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
    @NotBlank(message = "请输入用户名")
    // 用户名限制：小于16个字符，包含文字、字母、数字、下划线和减号
    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9-]{1,16}$", message = "用户名只能由中文、英文、数字、下划线、减号组成，长度为1-16个字符")
    String username;

    @NotBlank(message = "请输入密码")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,}$", message = "密码必须包含字母和数字，且长度不小于6位")
    String password;

    @NotBlank(message = "请再次输入密码")
    String confirmPassword;

    // 可以为null，不能为""或者" "
    @Pattern(regexp = "^[1][3,4,5,6,7,8,9][0-9]{9}$", message = "请输入正确的手机号")
    String phone;

    @NotBlank(message = "请输入验证码")
    String code;
}
