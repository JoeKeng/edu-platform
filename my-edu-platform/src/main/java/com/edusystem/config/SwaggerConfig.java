
package com.edusystem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

/**
 * Swagger 配置
 * <p>
 *
 * @author haoxr
 * @see <a href="https://doc.xiaominfo.com/docs/quick-start">knife4j 快速开始</a>
 * @since 2023/2/17
 */
@Configuration
@Slf4j
@RequiredArgsConstructor
public class SwaggerConfig {

    /**
     * 接口信息
     */
    @Bean
    public OpenAPI openApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("youlai-boot 接口文档")
                        .version("2.5.1")
                )
                // 配置全局鉴权参数-Authorize
                .components(new Components()
                        .addSecuritySchemes(HttpHeaders.AUTHORIZATION,
                                new SecurityScheme()
                                        .name(HttpHeaders.AUTHORIZATION)
                                        .type(SecurityScheme.Type.APIKEY)
                                        .in(SecurityScheme.In.HEADER)
                                        .scheme("Bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }


    /**
     * 全局自定义扩展
     * <p>
     * 在OpenAPI规范中，Operation 是一个表示 API 端点（Endpoint）或操作的对象。
     * 每个路径（Path）对象可以包含一个或多个 Operation 对象，用于描述与该路径相关联的不同 HTTP 方法（例如 GET、POST、PUT 等）。
     */
    @Bean
    public GlobalOpenApiCustomizer globalOpenApiCustomizer() {
        return openApi -> {
            // 全局添加鉴权参数
            if (openApi.getPaths() != null) {
                openApi.getPaths().forEach((s, pathItem) -> {
                    // 接口添加鉴权参数
                    pathItem.readOperations()
                            .forEach(operation ->
                                    operation.addSecurityItem(new SecurityRequirement().addList(HttpHeaders.AUTHORIZATION))
                            );
                });
            }
        };
    }

}

