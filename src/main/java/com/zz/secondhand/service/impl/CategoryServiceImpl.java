package com.zz.secondhand.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.Category;
import com.zz.secondhand.repository.CategoryRepository;
import com.zz.secondhand.service.ICategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("ICategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    CategoryRepository categoryRepository;

    //递归查询本节点id和孩子节点id
    public ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet,categoryId);
        List<Integer> categoryIdList = Lists.newArrayList();
        if(categoryId!=null){
            for(Category c:categorySet){
                categoryIdList.add(c.getId());
            }
        }
        return ServerResponse.creatBySuccess(categoryIdList);
    }

    //递归
    private Set<Category> findChildCategory(Set<Category> categorySet,Integer categoryId){
        Category category = categoryRepository.findById(categoryId).orElse(null);
        if(category != null){
            categorySet.add(category);
        }
        //查找子节点
        List<Category> categoryList = categoryRepository.findAllByParentId(categoryId);
        for(Category c : categoryList){
            findChildCategory(categorySet,c.getId());
        }
        return categorySet;
    }
}
