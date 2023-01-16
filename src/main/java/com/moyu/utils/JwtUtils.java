package com.moyu.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;


@Slf4j
@Component
@Data
@ConfigurationProperties(prefix = "moyusoldier.jwt")
public class JwtUtils {

    // 加载配置文件中的jwt配置
    private String key;
    private long expire;
    private String header;

    // 生成密钥
    public Key generalKey() {
        return new SecretKeySpec(key.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }

    // 生成jwt
    public String generateToken(long userId) {

        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);

        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .setSubject(userId + "")
                .setIssuedAt(nowDate)
                .setExpiration(expireDate)
                .signWith(generalKey())
                .compact();
    }

    // 从jwt中获取载荷，能获取到就相当于校验成功，过程详见https://www.jianshu.com/p/6bfeb86885a3
    public Claims getClaimByToken(String jwt) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(generalKey())
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
        } catch (Exception e) {
            log.error("token校验失败", e);
            return null;
        }
    }

    // token是否过期
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
}
