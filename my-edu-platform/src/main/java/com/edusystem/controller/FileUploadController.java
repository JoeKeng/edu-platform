package com.edusystem.controller;

import com.edusystem.model.FileRecord;
import com.edusystem.model.Result;
import com.edusystem.service.FileRecordService;
import com.edusystem.util.AliyunOssUtil;
import com.edusystem.util.CurrentHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.edusystem.util.CurrentHolder.getCurrentId;

@Slf4j
@RestController
@RequestMapping("/upload")
@Tag(name = "文件上传")
public class FileUploadController {

    @Autowired
    private AliyunOssUtil aliyunOssUtil;
    @Autowired
    private FileRecordService fileRecordService;

    /**
     * 通用文件上传接口
     * @param file 上传的文件
     * @param type 文件类型（avatars, course-covers, resources）
     */
    @Operation(summary = "通用文件上传接口")
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

        // 生成文件存储路径
        String filePath = type + "/" + file.getOriginalFilename();

        // 保存文件信息到数据库
        // 获取当前登录用户 ID
        Long uploaderId = Long.valueOf(CurrentHolder.getCurrentId());

        FileRecord fileRecord = new FileRecord();
        fileRecord.setFileName(file.getOriginalFilename());
        fileRecord.setFilePath(filePath);
        fileRecord.setFileUrl(fileUrl);
        fileRecord.setFileType(type);
        fileRecord.setUploaderId(uploaderId);
        fileRecordService.saveFileRecord(fileRecord);

        return Result.success(fileUrl);
    }


    /**
     * 获取文件下载 URL
     * @param type 文件类型（avatars, course-covers, resources）
     * @param fileName 文件名（完整文件名，如 "uuid.pdf"）
     * @return 临时下载 URL
     */
    @Operation(summary = "获取文件下载 URL")
    @GetMapping("/{type}/download")
    public Result getDownloadUrl(@PathVariable("type") String type,
                                 @RequestParam("fileName") String fileName) {
        log.info("获取下载 URL，文件类型：{}, 文件名：{}", type, fileName);

        // 限制只能下载指定类型的文件
//        if (!type.matches("avatars|course-covers|resources")) {
//            return Result.error("非法文件类型");
//        }

        // 生成 OSS 存储的文件路径
        //还有日期
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM"));
        String filePath = type + "/"+ datePath+ "/" + fileName;

        // 生成下载 URL（有效期 10 分钟）
        String downloadUrl = aliyunOssUtil.generateDownloadUrl(filePath, 10);
        if (downloadUrl == null) {
            return Result.error("获取下载链接失败");
        }

        return Result.success(downloadUrl);
    }

    // 删除文件
    @Operation(summary = "删除文件")
    @DeleteMapping("/{fileId}")
    public Result deleteFile(@PathVariable("fileId") Long fileId) {
        try {
            fileRecordService.deleteFile(fileId);
            return Result.success("文件删除成功");
        } catch (Exception e) {
            return Result.error("文件删除失败: " + e.getMessage());
        }
    }

    // 批量删除文件
    @Operation(summary = "批量删除文件")
    @DeleteMapping("/batch")
    public Result batchDeleteFiles(@RequestBody List<Long> fileIds) {
        try {
            fileRecordService.batchDeleteFiles(fileIds);
            return Result.success("文件批量删除成功");
        } catch (Exception e) {
            return Result.error("文件批量删除失败: " + e.getMessage());
        }
    }



}

