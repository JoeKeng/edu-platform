package com.edusystem.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * List类型处理器
 * 用于处理Java中的List类型与数据库字段之间的转换
 * 继承自BaseTypeHandler,实现MyBatis的类型转换接口
 */
@MappedTypes(List.class)  // 标注此处理器用于处理List类型
public class ListTypeHandler extends BaseTypeHandler<List<?>> {
    
    /**
     * 设置非空参数
     * 用于将Java List类型转换为数据库支持的类型并设置到PreparedStatement中
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<?> parameter, JdbcType jdbcType) throws SQLException {
        // 待实现:可以将List转换为JSON字符串存储
    }

    /**
     * 根据列名获取可为空的结果
     * 从ResultSet中获取数据并转换为List类型
     */
    @Override
    public List<?> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return List.of(); // 返回空List,待实现具体转换逻辑
    }

    /**
     * 根据列索引获取可为空的结果
     * 从ResultSet中获取数据并转换为List类型
     */
    @Override
    public List<?> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return List.of(); // 返回空List,待实现具体转换逻辑
    }

    /**
     * 从CallableStatement中获取可为空的结果
     * 用于存储过程的输出参数转换
     */
    @Override
    public List<?> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return List.of(); // 返回空List,待实现具体转换逻辑
    }
    // 实现类型转换逻辑（如JSON序列化）
}
   