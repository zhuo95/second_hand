package com.zz.secondhand.service;

import com.zz.secondhand.common.ServerResponse;
import com.zz.secondhand.entity.User;

public interface IUserService {
    ServerResponse login(String username, String password);

    ServerResponse<String> register(User user);

    ServerResponse<User> getUserInfoById(Long id);
}
