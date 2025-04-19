package com.edusystem.service;

import com.edusystem.model.FileRecord;
import java.util.List;

public interface FileRecordService {
    void saveFileRecord(FileRecord fileRecord);
    FileRecord getFileById(Long id);
    List<FileRecord> getFilesByUploader(Long uploaderId);
    void deleteFile(Long fileId);
    void batchDeleteFiles(List<Long> fileIds);
}
