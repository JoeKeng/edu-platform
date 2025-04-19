package com.edusystem.service.impl;

import com.edusystem.mapper.FileRecordMapper;
import com.edusystem.model.FileRecord;
import com.edusystem.service.FileRecordService;
import com.edusystem.util.AliyunOssUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileRecordServiceImpl implements FileRecordService {

    @Autowired
    private FileRecordMapper fileRecordMapper;
    private AliyunOssUtil aliyunOssUtil;

    @Override
    public void saveFileRecord(FileRecord fileRecord) {
        fileRecordMapper.insertFileRecord(fileRecord);
    }

    @Override
    public FileRecord getFileById(Long id) {
        return fileRecordMapper.getFileById(id);
    }

    @Override
    public List<FileRecord> getFilesByUploader(Long uploaderId) {
        return fileRecordMapper.getFilesByUploader(uploaderId);
    }


    @Override
    public void deleteFile(Long fileId) {
        FileRecord fileRecord = fileRecordMapper.getFileById(fileId);
        if (fileRecord == null) {
            throw new RuntimeException("文件不存在");
        }

        // 删除 OSS 文件
        boolean ossDeleted = aliyunOssUtil.deleteFile(fileRecord.getFilePath());

        if (ossDeleted) {
            // 删除数据库记录
            fileRecordMapper.deleteFileRecord(fileId);
        } else {
            throw new RuntimeException("OSS 文件删除失败");
        }
    }

    @Override
    public void batchDeleteFiles(List<Long> fileIds) {
        if (fileIds == null || fileIds.isEmpty()) {
            throw new RuntimeException("文件 ID 不能为空");
        }

        List<FileRecord> fileRecords = fileRecordMapper.getFilesByIds(fileIds);
        if (fileRecords == null || fileRecords.isEmpty()) {
            throw new RuntimeException("未找到对应的文件记录");
        }

        List<String> filePaths = fileRecords.stream()
                .map(FileRecord::getFilePath)
                .collect(Collectors.toList());

        // 删除 OSS 文件
        boolean ossDeleted = aliyunOssUtil.deleteFiles(filePaths);

        if (ossDeleted) {
            // 删除数据库记录
            fileRecordMapper.batchDeleteFileRecords(fileIds);
        } else {
            throw new RuntimeException("OSS 文件批量删除失败");
        }
    }

}
