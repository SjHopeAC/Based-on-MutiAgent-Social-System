package com.example.helloworld.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

@RestController
public class upLoadController {
    //用户头像
    @PostMapping("/upload")
    public String getFile(MultipartFile image, HttpServletRequest request) throws IOException {
        String path = System.getProperty("user.dir") + File.separator + "upload" + File.separator;
        System.out.println(path);
        saveFile(image,path);

        System.out.println(image.getContentType());
        System.out.println("文件大小"+image.getSize());
        System.out.println(image.getName());
        return "上传成功";
    }

    private void saveFile(MultipartFile image,String path) throws IOException {
        File dir=new File(path);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(path+image.getOriginalFilename());
        image.transferTo(file);
    }

    // 帖子图片上传接口：完整路径 /api/upload/post
    @PostMapping("/upload/post")
    public String uploadPostImg(
            @RequestParam("postImg") MultipartFile postImg,       // 前端传的图片文件
            @RequestParam("userId") String userId,               // 前端传的用户ID（备用）
            @RequestParam("filename") String filename,           // 前端传的唯一文件名（核心）
            HttpServletRequest request) {

        // 1. 配置基础路径（建议从配置文件读取）
        String basePath = System.getProperty("user.dir") + File.separator + "upload" + File.separator + "post";


        // 2. 创建目录（确保多级目录存在）
        File dir = new File(basePath);
        if (!dir.exists()) {
            boolean isMkSuccess = dir.mkdirs();
            System.out.println("目录创建结果：" + (isMkSuccess ? "成功" : "失败") + "，路径：" + basePath);
        }

        try {
            // 3. 基础校验：文件不能为空
            if (postImg.isEmpty()) {
                return "上传失败：图片文件为空";
            }

            // 4. 根据用户ID判断是否为VIP用户，设置不同的上传限制
            long maxSizeBytes = 5 * 1024 * 1024; // 默认5MB
            
            // 这里可以根据实际情况从数据库查询用户的VIP状态
            // 暂时简化处理，假设用户ID大于1000的为VIP用户
            try {
                long uid = Long.parseLong(userId);
                if (uid > 1000) {
                    maxSizeBytes = 50 * 1024 * 1024; // VIP用户50MB
                }
            } catch (NumberFormatException e) {
                // 用户ID格式错误，使用默认限制
            }

            // 5. 校验文件大小
            if (postImg.getSize() > maxSizeBytes) {
                return "上传失败：图片大小超过限制";
            }

            // 6. 拼接最终文件路径（直接使用前端传的filename作为文件名）
            File targetFile = new File(dir, filename); // 关键：用前端传的filename作为文件名

            // 7. 保存文件到本地
            postImg.transferTo(targetFile);

            // 8. 调试信息（方便排查）
            System.out.println("✅ 图片上传成功：");
            System.out.println("  - 保存路径：" + targetFile.getAbsolutePath());
            System.out.println("  - 文件名：" + filename);
            System.out.println("  - 文件大小：" + postImg.getSize() + " 字节");
            System.out.println("  - 文件类型：" + postImg.getContentType());

            return "图片上传成功";

        } catch (IOException e) {
            // 捕获具体IO异常，返回友好提示
            System.err.println("❌ 图片上传失败：");
            e.printStackTrace();
            return "上传失败：" + e.getMessage();
        } catch (Exception e) {
            // 捕获所有未知异常
            System.err.println("❌ 上传未知错误：");
            e.printStackTrace();
            return "上传失败：未知错误";
        }
    }
}
