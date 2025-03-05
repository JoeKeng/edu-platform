package com.edusystem.controller;

import com.edusystem.model.Result;
import com.edusystem.util.AliyunOssUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Autowired
    private AliyunOssUtil aliyunOssUtil;

    /**
     * 通用文件上传接口
     * @param file 上传的文件
     * @param type 文件类型（avatars, course-covers, resources）
     */
    @PostMapping("/{type}")
    public Result uploadFile(@RequestParam("file") MultipartFile file,
                             @PathVariable("type") String type) throws IOException {
        log.info("上传文件类型：{}", type);
        log.info("上传文件：{}", file.getOriginalFilename());
        if (file.isEmpty()) {
            return Result.error("文件不能为空");
        }

        // 限制上传类型
        if (!type.matches("avatars|course-covers|resources")) {
            return Result.error("非法上传类型");
        }

        // 调用 OSS 上传
        String fileUrl = aliyunOssUtil.uploadFile(file.getBytes(),file.getOriginalFilename(),type);
        log.info("文件上传OSS,URL: {}",fileUrl);
//        String fileUrl = aliyunOssUtil.uploadFile(file, type);
        if (fileUrl == null) {
            return Result.error("文件上传失败");
        }

        return Result.success(fileUrl);
    }
}

