package com.zz.secondhand;

import com.zz.secondhand.controller.common.SessionExpireFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class SecondHandApplication extends SpringBootServletInitializer{

	//打包成war
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SecondHandApplication.class);
	}

	//配置fliter
	@Bean
	public FilterRegistrationBean httpFilter(){
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		//设置filter
		registrationBean.setFilter(new SessionExpireFilter());
		//定义拦截url
		registrationBean.addUrlPatterns("/");
		return registrationBean;
	}

	public static void main(String[] args) {
		SpringApplication.run(SecondHandApplication.class, args);
	}



}
