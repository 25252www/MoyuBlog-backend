package com.moyu.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moyu.common.dto.BlogDto;
import com.moyu.common.lang.Result;
import com.moyu.pojo.Blog;
import com.moyu.service.blog.BlogService;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequestMapping("/blogs")
@RestController
public class BlogController{

    @Autowired
    private BlogService blogService;

    @GetMapping
    public Result selectOnePage(@RequestParam(defaultValue = "1") Integer currentPage) {

        //一页展示7条数据
        Page page = new Page(currentPage, 7);
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().orderByDesc("date"));

        return Result.succ(pageData);
    }

    @GetMapping("/all")
    public Result selectAll(){
        //返回除了discription和content的所有列
        List<Blog> blogList = blogService.list(new QueryWrapper<Blog>().select("id", "title","hits","date").orderByDesc("date"));
        List<BlogDto> blogDtoList = blogList.stream().map(blog -> {
            BlogDto blogDto = new BlogDto();
            BeanUtil.copyProperties(blog, blogDto);
            return blogDto;
        }).collect(Collectors.toList());
        return Result.succ(blogDtoList);
    }

    @GetMapping("/{id}")
    public Result getById(@PathVariable(name = "id") Integer id) {
        Blog blog = blogService.getById(id);
        Assert.notNull(blog, "该博客不存在");
        //点击量+1
        blogService.update(null, new UpdateWrapper<Blog>().eq("id", blog.getId()).set("hits",blog.getHits()+1));

        return Result.succ(blog);
    }

    @RequiresAuthentication
    @PostMapping("/edit")
    public Result edit(@Validated @RequestBody Blog blog) {
        Blog temp = null;
        //id不为空，是编辑
        if(blog.getId() != null) {
            temp = blogService.getById(blog.getId());
            //编辑文章日期不变
            BeanUtil.copyProperties(blog, temp, "id", "date");
        }
        //添加新文章
        else {
            temp = new Blog();
            temp.setDate(LocalDateTime.now());
            //新增文章，日期为当前时间
            BeanUtil.copyProperties(blog, temp, "id");
        }
        blogService.saveOrUpdate(temp);
        return Result.succ(null);
    }
    @GetMapping("/delete/{id}")
    public Result removeById(@PathVariable(name = "id") Integer id){
        boolean b = blogService.removeById(id);
        Assert.isTrue(b,"删除失败");
        return Result.succ(null);
    }
}
