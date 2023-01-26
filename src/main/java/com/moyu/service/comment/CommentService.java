package com.moyu.service.comment;

import com.baomidou.mybatisplus.extension.service.IService;
import com.moyu.DO.Comment;
import com.moyu.common.dto.CommentDTO;

import java.util.List;


public interface CommentService extends IService<Comment> {
    List<CommentDTO> generateCommentDTOListByBlogId(Integer blogId);
}

