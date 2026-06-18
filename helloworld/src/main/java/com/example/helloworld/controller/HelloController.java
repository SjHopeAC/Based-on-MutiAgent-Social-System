package com.example.helloworld.controller;

import com.example.helloworld.entity.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;

@RestController
//@Controller 这个注解默认会找到视图，返回视图名称，如果要返回json类数据，则需要加上@ResponseBody，而restful模式则默认返回数据
public class HelloController {

// http://localhost:8080/hello?nickname=zhangsan&phone=123
    @RequestMapping(value="/hello",method = RequestMethod.GET)
    // @GetMapping("/hello")固定get方式，你是post
    public String hello(String name,String phone){
        return name+phone;
    }


}
