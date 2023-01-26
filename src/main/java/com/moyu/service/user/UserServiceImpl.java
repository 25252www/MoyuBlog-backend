package com.moyu.service.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.mapper.UserMapper;
import com.moyu.DO.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
