package fr.abes.sudoc.configuration;


import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableCaching
@EnableJpaRepositories(
		basePackages = {"fr.abes.sudoc.repository", "fr.abes.sudoc.entity"})
public class BaseXMLOracleConfig  {

}
