# MoyuBlog -BackEnd
[摸鱼战士的小站](http://moyusoldier.cn)-后端

## 注
本博客为微服务架构，本代码库仅实现博客相关核心功能

## 技术栈
- Springboot 
- MySQL 
- MyBatis-plus
- shiro&jwt
- Swagger2

## 功能

- [x] 博客CRUD
- [x] 用户登录注册
- [x] 权限控制
- [x] 后台管理
- [x] 日志服务

## 本地启动

1. git clone
2. 修改或补全配置，配置文件如下
```
src/main/resources/application*.yml
src/main/resources/logback.xml // 主要是日志存储位置，注意docker-compose中的配置需要同步更改
```
3. mvn clean install
4. run






