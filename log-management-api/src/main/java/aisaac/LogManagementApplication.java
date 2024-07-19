package aisaac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableAutoConfiguration
@EnableJpaRepositories(basePackages="aisaac.dao")
@SpringBootApplication
public class LogManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(LogManagementApplication.class, args);
	}

}
