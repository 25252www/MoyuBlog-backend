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

import java.time.LocalDateTime;
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
        // 查询数据库不返回content，加快速度
        IPage pageData = blogService.page(page, new QueryWrapper<Blog>().select("id", "title","hits","date","description").orderByDesc("date"));
        List<BlogDTO> blogDTOList = (List<BlogDTO>) pageData.getRecords().stream().map(blog -> {
            BlogDTO blogDto = new BlogDTO();
            BeanUtil.copyProperties(blog, blogDto);
            return blogDto;
        }).collect(Collectors.toList());
        pageData.setRecords(blogDTOList);
        return Result.succ(pageData);
    }

    @GetMapping("/all")
    public Result selectAll(){
        //返回除了content的所有列
        List<Blog> blogList = blogService.list(new QueryWrapper<Blog>().select("id", "title","hits","date","description").orderByDesc("date"));
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

    @RequiresAuthentication
    @RequiresRoles("admin")
    @GetMapping("/delete/{id}")
    public Result removeById(@PathVariable(name = "id") Integer id){
        boolean b = blogService.removeById(id);
        Assert.isTrue(b,"删除失败");
        return Result.succ(null);
    }
}
