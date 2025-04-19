package com.edusystem.task;

import com.edusystem.service.ClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 班级人数定时更新任务
 * 定期检查并更新所有班级的人数，确保班级人数与实际学生数量一致
 */
@Component
@EnableScheduling
@Slf4j
public class ClassSizeUpdateTask {

    @Autowired
    private ClassService classService;
    
    /**
     * 每天凌晨2点执行班级人数更新
     * 选择凌晨时间段执行，避免影响系统正常使用
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void updateAllClassSizes() {
        log.info("开始执行班级人数定时更新任务");
        try {
            classService.updateAllClassSizes();
            log.info("班级人数定时更新任务执行完成");
        } catch (Exception e) {
            log.error("班级人数定时更新任务执行失败", e);
        }
    }
}