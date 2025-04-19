package com.edusystem.model;

import lombok.Data;
import java.util.Date;

@Data
public class FileRecord {
    private Long id;          // 文件记录 ID
    private String fileName;  // 文件名
    private String filePath;  // OSS 上的存储路径
    private String fileUrl;   // OSS 访问 URL
    private String fileType;  // 文件类型（avatars, course-covers, resources）
    private Long uploaderId;  // 上传者 ID
    private Date uploadTime;  // 上传时间
}
