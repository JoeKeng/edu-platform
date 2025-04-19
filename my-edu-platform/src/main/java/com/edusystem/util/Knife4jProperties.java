package com.edusystem.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Data
@Configuration
@ConfigurationProperties("knife4j")
public class Knife4jProperties{

    /**
     * 标题
     **/
    private String title = "demo";

    /**
     * 网关
     */
    private String gateway;

    /**
     * 获取token
     */
    private String tokenUrl;

    /**
     * 作用域
     */
    private String scope;

    private Map<String, String> services;

    /**
     * 作者
     **/
    private String author = "Will";

    /**
     * 描述
     **/
    private String description = "接口文档";

    /**
     * 版本
     **/
    private String version = "v1.0.0";

}
