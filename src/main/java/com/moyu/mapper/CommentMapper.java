package com.moyu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.moyu.DO.Comment;
import com.moyu.common.dto.CommentDTO;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    @Select("SELECT c.*, u.username, u.avatar FROM t_comment c LEFT JOIN t_user u ON c.user_id = u.id WHERE c.blog_id = #{blogId} and c.deleted = 0 ORDER BY c.create_time")
    List<CommentDTO> generateCommentDTOListByBlogId(Integer blogId);
}
