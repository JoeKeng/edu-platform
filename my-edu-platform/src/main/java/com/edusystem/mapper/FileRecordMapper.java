package com.edusystem.mapper;

import com.edusystem.model.FileRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FileRecordMapper {
    void insertFileRecord(FileRecord fileRecord);
    FileRecord getFileById(Long id);
    List<FileRecord> getFilesByUploader(Long uploaderId);
    void deleteFileRecord(Long id);
    void deleteFileByPath(String filePath);
    void batchDeleteFileRecords(@Param("fileIds") List<Long> fileIds);
    void batchDeleteFilesByPath(@Param("filePaths") List<String> filePaths);

    List<FileRecord> getFilesByIds(List<Long> fileIds);
}
