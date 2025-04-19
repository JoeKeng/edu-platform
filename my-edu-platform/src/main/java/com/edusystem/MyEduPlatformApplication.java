package com.edusystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@lombok.extern.slf4j.Slf4j
@SpringBootApplication
@EnableConfigurationProperties
public class MyEduPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(MyEduPlatformApplication.class, args);
		log.info("接口地址==>http://localhost:9092/doc.html#/home");
	}

}
