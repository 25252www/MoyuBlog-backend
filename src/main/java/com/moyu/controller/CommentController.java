package com.moyu.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import com.moyu.DO.Comment;
import com.moyu.common.dto.CommentDTO;
import com.moyu.common.lang.Result;
import com.moyu.service.comment.CommentService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping("/comment/{blogId}")
    public Result generateCommentsByBlogId(@PathVariable("blogId") Integer blogId) {
        List<CommentDTO> commentsList = commentService.generateCommentDTOListByBlogId(blogId);
        List<CommentDTO> res = new ArrayList<>();
        // 时间倒序组织一级评论
        for (int i = commentsList.size() - 1; i >= 0; i--) {
            CommentDTO comment = commentsList.get(i);
            if (comment.getParentId() == null) {
                res.add(comment);
            }
        }
        // 时间正序组织二级评论
        for (CommentDTO comment : commentsList) {
            if (comment.getParentId() != null) {
                for (CommentDTO commentDTO : res) {
                    if (comment.getRootId().equals(commentDTO.getId())) {
                        if (commentDTO.getChildren() == null) {
                            commentDTO.setChildren(new ArrayList<>());
                        }
                        commentDTO.getChildren().add(comment);
                    }
                }
            }
        }
        return Result.succ(MapUtil.builder()
                .put("replies", res)
                .map());
    }

    @RequiresAuthentication
    @PostMapping("/comment/add")
    public Result addComment(@Validated @RequestBody Comment comment) {
        // todo:校验参数
//        System.out.println(comment);
        Boolean bool = commentService.save(comment);
        Assert.isTrue(bool, "评论失败");
        return Result.succ(null);
    }
}
