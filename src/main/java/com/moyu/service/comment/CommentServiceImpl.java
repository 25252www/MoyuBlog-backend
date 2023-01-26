package com.moyu.service.comment;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.moyu.common.dto.CommentDTO;
import com.moyu.mapper.CommentMapper;
import com.moyu.DO.Comment;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Override
    public List<CommentDTO> generateCommentDTOListByBlogId(Integer blogId) {
        return baseMapper.generateCommentDTOListByBlogId(blogId);
    }
}
