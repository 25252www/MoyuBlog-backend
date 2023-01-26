package com.moyu.service.blog;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.mapper.BlogMapper;
import com.moyu.DO.Blog;
import org.springframework.stereotype.Service;

@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements BlogService {

}
