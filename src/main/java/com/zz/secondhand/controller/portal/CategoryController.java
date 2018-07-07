package com.zz.secondhand.controller.portal;


import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.Category;
import com.zz.secondhand.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/category/")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    @ResponseBody
    public ServerResponse getAllParents(){
        List<Category> categories = categoryRepository.findAllByParentId(0);

        return ServerResponse.creatBySuccess(categories);
    }
}
