package com.charizard.compiled.hangout_service.infrastructure.config;

import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuración de Flyway para migraciones de base de datos.
 * Actualmente deshabilitada (pendiente de habilitar cuando se requieran
 * migraciones programáticas avanzadas). Las migraciones SQL se ejecutan
 * automáticamente mediante {@code spring.flyway.enabled=true} en application.properties.
 */
// @Configuration
public class FlywayConfig {

    // @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .baselineOnMigrate(true)
                .load();
    }
}