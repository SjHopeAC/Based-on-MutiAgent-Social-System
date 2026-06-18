package com.example.helloworld.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CrosConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")//允许跨域访问的路径
                .allowedOrigins("http://localhost:8080", "http://localhost:8081")//允许跨域访问的源
                .allowedMethods("GET", "HEAD", "POST", "PUT", "DELETE", "OPTIONS")//允许请求方法
                .allowCredentials(true)
                .maxAge(3600)//预检间隔时间(就是有效时间)
                .allowedHeaders("*");
    }
}
/**
 * 跨域配置(或者在RestController后面加一个@CrossOrigin)
 * 当开启了allowCredentials = true（允许前端携带 cookie、token 等凭证），Access-Control-Allow-Origin响应头不能设置为*（通配所有源），必须明确指定具体的源地址
 */