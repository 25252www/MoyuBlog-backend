package com.moyu.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moyu.common.dto.BlogDTO;
import com.moyu.common.lang.Result;
import com.moyu.DO.Blog;
import com.moyu.service.blog.BlogService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/blogs")
@RestController
public class BlogController{

    @Autowired
    BlogService blogService;

    @GetMapping
    public Result selectOnePage(@RequestParam(defaultValue = "1") Integer currentPage) {

        // 一页展示7条数据
        Page page = new Page(currentPage, 7);
        // 返回("id", "title","hits","create_time","description")，只查询deleted为0的数据，按照create_time降序排列
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().select("id", "title","hits","create_time","description").eq("deleted", 0).orderByDesc("create_time"));
        List<BlogDTO> blogDTOList = (List<BlogDTO>) pageData.getRecords().stream().map(blog -> {
            BlogDTO blogDto = new BlogDTO();
            BeanUtil.copyProperties(blog, blogDto);
            return blogDto;
        }).collect(Collectors.toList());
        pageData.setRecords(blogDTOList);
        return Result.succ(pageData);
    }

    @RequiresAuthentication
    @GetMapping("/all")
    public Result selectAll(){
        // 返回("id", "title","hits","create_time","description")，按照create_time降序排列
        List<Blog> blogList = blogService.list(new QueryWrapper<Blog>().select("id", "title","hits","create_time","description").orderByDesc("create_time"));
        List<BlogDTO> blogDTOList = blogList.stream().map(blog -> {
            BlogDTO blogDto = new BlogDTO();
            BeanUtil.copyProperties(blog, blogDto);
            return blogDto;
        }).collect(Collectors.toList());
        return Result.succ(blogDTOList);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable(name = "id") Integer id) {
        Blog blog = blogService.getById(id);
        Assert.notNull(blog, "博客不存在");
        //点击量+1
        blogService.update(null, new UpdateWrapper<Blog>().eq("id", blog.getId()).set("hits",blog.getHits()+1));

        return Result.succ(blog);
    }

    @RequiresAuthentication
    @RequiresRoles("admin")
    @PostMapping("/edit")
    // 前端只传来id, title, description, content，若id为空，则为新增文章，否则为编辑文章
    public Result edit(@Validated @RequestBody Blog blog) {
        boolean b = blogService.saveOrUpdate(blog);
        Assert.isTrue(b,"操作失败");
        return Result.succ(null);
    }

    @RequiresAuthentication
    @RequiresRoles("admin")
    @DeleteMapping("/{id}")
    public Result removeById(@PathVariable(name = "id") Integer id){
        // 逻辑删除
        boolean b = blogService.update(new UpdateWrapper<Blog>().eq("id", id).set("deleted", 1));
        Assert.isTrue(b,"删除失败");
        return Result.succ(null);
    }
}
