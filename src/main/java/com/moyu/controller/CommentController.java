package com.moyu.controller;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.moyu.DO.Comment;
import com.moyu.common.dto.CommentDTO;
import com.moyu.common.lang.Result;
import com.moyu.service.comment.CommentService;
import com.moyu.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@RestController
public class CommentController {

    @Autowired
    CommentService commentService;

    @GetMapping("/blogs/{blogId}/comments")
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
    @PostMapping("/comments")
    public Result addComment(@Validated @RequestBody Comment comment) {
        // todo:校验参数
//        System.out.println(comment);
        Boolean bool = commentService.save(comment);
        Assert.isTrue(bool, "评论失败");
        return Result.succ(null);
    }

    @RequiresAuthentication
    @DeleteMapping("/comments/{id}")
    public Result deleteComment(@PathVariable("id") Integer id) {
        // 只有当前用户为admin或者是评论的作者，才能删除评论
        AccountProfile accountProfile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();
        if(!accountProfile.getId().equals(commentService.getById(id).getUserId())) {
            SecurityUtils.getSubject().checkRole("admin");
        }
        // 非递归获得该评论及其子评论的id
        Queue<Integer> queue = new LinkedList<>();
        List<Integer> commentIds = new ArrayList<>();
        queue.add(id);
        while (!queue.isEmpty()) {
            Integer commentId = queue.poll();
            commentIds.add(commentId);
            List<Comment> comments = commentService.list(new UpdateWrapper<Comment>().eq("parent_id", commentId));
            for (Comment comment : comments) {
                queue.add(comment.getId());
            }
        }

        // 批量更新deleted字段为1
        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("id", commentIds);
        updateWrapper.set("deleted", 1);
        Boolean bool = commentService.update(updateWrapper);

        Assert.isTrue(bool, "删除失败");
        return Result.succ(null);
    }
}
