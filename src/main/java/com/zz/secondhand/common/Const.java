package com.zz.secondhand.common;

public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";

    public static final String USER_NAME = "username";

    public interface RedisCacheExTime{
        int REDIS_SESSION_TIME = 60*30; //30分钟
    }

    public interface Role {
        int ROLE_CUSTOMER = 0; //普通用户
        int ROLE_ADMIN = 1; //管理员
    }


    public interface Gender{
        int GENDER_FEMALE = 0;
        int GENDER_MALE = 1;
    }
}