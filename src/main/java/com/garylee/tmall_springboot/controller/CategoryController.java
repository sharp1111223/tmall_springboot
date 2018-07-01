package com.garylee.tmall_springboot.controller;

import com.garylee.tmall_springboot.domain.Category;
import com.garylee.tmall_springboot.service.CategoryService;
import com.garylee.tmall_springboot.util.FileUp;
import com.garylee.tmall_springboot.util.ImageUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
//分类管理界面
@Controller
public class CategoryController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    FileUp fileUp;
    @RequestMapping("listCategory")
    @ResponseBody
    /**
     * @start 当前页数
     * @size 每页个数
     */
    public PageInfo getAll(@RequestParam(value = "start",defaultValue = "0")int start,@RequestParam(value = "size",defaultValue = "5")int size){
        PageHelper.startPage(start,size);
        PageInfo<Category> page = new PageInfo<>(categoryService.listAll());
        return page;
    }
    //添加新的分类
    @RequestMapping("admin_category_add")
    public String add(Category category, @RequestParam("image") MultipartFile multipartFile) throws IOException {
        //添加到数据库
        categoryService.add(category);
        //保存本地、上传文件
        categoryService.up(multipartFile,category);
        return "redirect:/admin_category_list";
    }
    //删除已有分类
    @RequestMapping("admin_category_delete")
    public String delete(int id){
        categoryService.delete(id);
        //删除七牛空间中的文件
        fileUp.delete(id+".jpg");
        //删除本地文件
        File imageFolder= new File("d:\\Users\\Administrator\\Desktop\\tmall_image\\category");
        File file = new File(imageFolder,id+".jpg");
        file.delete();
        return "redirect:/admin_category_list";
    }
    @RequestMapping("getCategory")
    @ResponseBody
    public Category get(int id){
        return categoryService.get(id);
    }
    //更新已有分类
    // TODO: 2018/7/1 更新分类图片时七牛云上不会刷新缓存(即为原资源)
    @RequestMapping("admin_category_update")
    public String update(Category category,@RequestParam("image") MultipartFile multipartFile) throws IOException {
        categoryService.update(category);
        categoryService.up(multipartFile, category);
        fileUp.refresh(category.getId());
        return "redirect:/admin_category_list";
    }
}
