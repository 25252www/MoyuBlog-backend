package com.moyu.service.blog;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.mapper.BlogMapper;
import com.moyu.pojo.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

}
