package com.edusystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页结果封装类
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    //根据接口文档写属性名
    private Long total;
    private List<T> rows;

}