package com.zz.secondhand.controller.manage;

import com.zz.secondhand.common.Const;
import com.zz.secondhand.common.ResponseCode;
import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.Category;
import com.zz.secondhand.entity.User;
import com.zz.secondhand.repository.CategoryRepository;
import com.zz.secondhand.service.IUserService;
import com.zz.secondhand.util.CookieUtil;
import com.zz.secondhand.util.JsonUtil;
import com.zz.secondhand.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;


@Controller
@RequestMapping("/manage/category")
public class CategoryManagerController {
    @Autowired
    private IUserService userService;
    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping
    @ResponseBody
    public ServerResponse addCategory(HttpServletRequest request, String categoryName, @RequestParam(value = "parentId",defaultValue = "0") int parentId){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null||user.getRole()== Const.Role.ROLE_CUSTOMER){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        //新建
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        Date now = new Date();
        category.setCreateTime(now);
        category.setUpdateTime(now);

        categoryRepository.save(category);
        return ServerResponse.creatBySuccessMessage("操作成功");
    }

    @DeleteMapping
    @ResponseBody
    public ServerResponse deleteCategoryById(int id,HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null||user.getRole()== Const.Role.ROLE_CUSTOMER){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        Category category = categoryRepository.findById(id).orElse(null);
        if(category==null) return ServerResponse.creatByErrorMessage("没有该类");
        categoryRepository.delete(category);

        return ServerResponse.creatBySuccess("操作成功");
    }
}
