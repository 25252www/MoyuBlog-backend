package com.moyu.service.user;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.mapper.UserMapper;
import com.moyu.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
