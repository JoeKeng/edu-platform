package com.edusystem.config;
import java.sql.Driver;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Config;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Logging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.beans.factory.annotation.Value;
@Configuration
public class Neo4jConfig {

    @Value("${spring.neo4j.uri}")
    private String uri;

    @Value("${spring.neo4j.authentication.username}")
    private String username;

    @Value("${spring.neo4j.authentication.password}")
    private String password;

    public Driver neo4jDriver() {
        Config config = Config.builder()
                .withLogging(Logging.slf4j())
                .build();

        return (Driver) GraphDatabase.driver(uri,
                AuthTokens.basic(username, password),
                config);
    }
}