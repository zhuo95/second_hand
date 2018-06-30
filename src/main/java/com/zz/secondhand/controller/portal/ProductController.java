package com.zz.secondhand.controller.portal;

import com.google.common.collect.Maps;
import com.zz.secondhand.common.ResponseCode;
import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.Product;
import com.zz.secondhand.entity.User;
import com.zz.secondhand.service.IFileService;
import com.zz.secondhand.service.IProductService;
import com.zz.secondhand.util.CookieUtil;
import com.zz.secondhand.util.JsonUtil;
import com.zz.secondhand.util.PropertyUtil;
import com.zz.secondhand.util.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;
    @Autowired
    private IFileService fileService;

    //获取detail
    @GetMapping("{id}")
    @ResponseBody
    public ServerResponse getDetail(@PathVariable("id") Long id){
        return iProductService.getProductDetail(id);
    }

    //搜索,点击分类显示 或者按keyword搜索
    @GetMapping("list")
    @ResponseBody
    public ServerResponse list(@RequestParam(value = "keyword",required = false)String keyord,
                               @RequestParam(value = "categoryId",required = false)Integer categoryId,
                               @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                               @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        return iProductService.list(keyord,categoryId,pageNum,pageSize);
    }

    /**
     * ======================================
     *
     * 用户买卖模块
     *
     * ======================================
     */

    //出售
    @PostMapping("sell")
    @ResponseBody
    public ServerResponse sell(Product product,HttpServletRequest request){
        //判断session
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);

        if(user==null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        return iProductService.sell(product,user);
    }

    //upload img
    @PostMapping("upload")
    @ResponseBody
    public ServerResponse uploadImg(Integer productId,@RequestParam(value = "upload_file",required = false)MultipartFile multipartFile, HttpServletRequest request){
        //判断session
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        //逻辑部分
        String path = request.getSession().getServletContext().getRealPath("upload");
        String fileName = multipartFile.getOriginalFilename();
        if(StringUtils.isBlank(fileName)||fileName.lastIndexOf('.')==-1) return ServerResponse.creatByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        String postfix = fileName.substring(fileName.lastIndexOf('.'),fileName.length()).toLowerCase();
        if(!StringUtils.equals(postfix,".jpg")&&!StringUtils.equals(postfix,".jpeg")&&!StringUtils.equals(postfix,".png")){
            return ServerResponse.creatByErrorMessage("请上传图片");
        }
        //上传
        String targetFileName = fileService.upload(multipartFile,path);
        String url = PropertyUtil.getProperty("ftp.server.http.prefix")+targetFileName;
        //设置返回
        Map fileMap = Maps.newHashMap();
        fileMap.put("uri",targetFileName);
        fileMap.put("url",url);
        return ServerResponse.creatBySuccess(fileMap);
    }

    //出售完毕后删除
    @DeleteMapping("{id}")
    @ResponseBody
    public ServerResponse deleteByProductId(@RequestParam(value = "id")Long id, HttpServletRequest request){
        //判断session
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }

       return iProductService.del(id,user.getId());

    }

    //购买
    @PostMapping({"{id}"})
    @ResponseBody
    public ServerResponse buyByProductId(@RequestParam(value = "id")Long id, HttpServletRequest request){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = JsonUtil.string2Obj(userJsonStr,User.class);
        if(user==null){
            return ServerResponse.creatByErrorCodeMessage(ResponseCode.NEEDLOG_IN.getCode(),ResponseCode.NEEDLOG_IN.getDesc());
        }

        return iProductService.buyByProductId(id,user.getId());
    }


}
