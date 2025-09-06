package com.branch.demo.config;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HibernateConfig {

    @Bean
    public PhysicalNamingStrategy physicalNamingStrategy() {
        return new PhysicalNamingStrategy() {
            @Override
            public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
                return name;
            }

            @Override
            public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
                return name;
            }

            @Override
            public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
                return name;
            }

            @Override
            public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
                return name;
            }

            @Override
            public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
                return name;
            }
        };
    }
}