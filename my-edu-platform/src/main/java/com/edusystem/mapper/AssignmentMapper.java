package com.edusystem.mapper;

import com.edusystem.dto.NewAssignmentDTO;
import com.edusystem.model.Assignment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AssignmentMapper {

    @Insert("INSERT INTO assignment (teacher_id, course_id, chapter_id, title, description, max_attempts, start_time, end_time) " +
            "VALUES (#{teacherId}, #{courseId},#{chapterId} , #{title}, #{description}, #{maxAttempts}, #{startTime}, #{endTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(NewAssignmentDTO NewAssignmentDTO);

    @Select("SELECT * FROM assignment WHERE id = #{id}")
    Assignment getById(Integer id);

    @Select("SELECT * FROM assignment WHERE course_id = #{courseId}")
    List<Assignment> getByCourseId(@Param("courseId") Integer courseId);


    /**
     * 更新作业信息
     * @param assignment 包含更新信息的作业对象，其属性值将被用于构建更新语句
     * @return 返回更新操作影响的行数，通常为1表示成功更新，0表示未找到对应的作业记录
     */
    @Update("UPDATE assignment SET title=#{title}, description=#{description}, max_attempts=#{maxAttempts}, start_time=#{startTime}, end_time=#{endTime} WHERE id=#{id}")
    int update(Assignment assignment);

    @Delete("DELETE FROM assignment WHERE id = #{id}")
    int delete(Integer id);

    @Select("SELECT * FROM assignment WHERE chapter_id = #{chapterId}")
    List<Assignment> getByChapterId(Integer chapterId);

    @Select("SELECT * FROM assignment WHERE teacher_id = #{teacherId} ORDER BY created_at DESC")
    List<Assignment> findByTeacherId(Long teacherId);
}

