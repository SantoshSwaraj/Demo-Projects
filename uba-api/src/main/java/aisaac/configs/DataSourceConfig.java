package aisaac.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import aisaac.util.SecurityUtils;

@Configuration
public class DataSourceConfig {
    
    @Autowired
    private Environment environment;
    
    @Bean
    public DataSource getDataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        //dataSourceBuilder.driverClassName(environment.getProperty("spring.datasource.driver-class-name"));
        dataSourceBuilder.url(environment.getProperty("spring.datasource.url"));
        dataSourceBuilder.username(environment.getProperty("spring.datasource.username"));
        String encryptedPassword = environment.getProperty("spring.datasource.password");
        String decryptedPassword = SecurityUtils.decrypt256Enc(encryptedPassword);
        dataSourceBuilder.password(decryptedPassword);
        return dataSourceBuilder.build();
    }

}
