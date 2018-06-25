package com.zz.secondhand.service.impl;

import com.zz.secondhand.common.Const;
import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.User;
import com.zz.secondhand.repository.UserRepository;
import com.zz.secondhand.service.IUserService;
import com.zz.secondhand.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("IUserService")
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;

    /**
     * log in
     * @param username
     * @param password
     * @return
     */
    public ServerResponse login(String username,String password){
        User user = userRepository.findByUsername(username);
        if(user==null){
            return ServerResponse.creatByErrorMessage("用户不存在");
        }
        //对比 MD5加密后的密码是否相同
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        if(!StringUtils.equals(md5Password,user.getPassword())){
            return ServerResponse.creatByErrorMessage("密码错误");
        }
        //把密码设置成空，不返回密码
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.creatBySuccess("登录成功",user);
    }


    /**
     * 检查是否注册过被用过
     * @param username
     * @return
     */
    public ServerResponse<String> checkValid(String username){
        if(StringUtils.isNotBlank(username)){
            //check
            User user = userRepository.findByUsername(username);
            if(user!=null) return ServerResponse.creatByErrorMessage("用户名已被注册");
        }else {
            return ServerResponse.creatByErrorMessage("请输入用户名");
        }
        return ServerResponse.creatBySuccessMessage("Success");
    }

    /**
     * 注册
     * @param user
     * @return
     */
    public ServerResponse<String> register(User user){
        ServerResponse validResponse = this.checkValid(user.getUsername());
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //MD5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        Date now = new Date();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userRepository.save(user);
        return ServerResponse.creatBySuccessMessage("注册成功");
    }


}
