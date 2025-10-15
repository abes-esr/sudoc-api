package fr.abes.sudoc.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
		basePackages = {"fr.abes.sudoc.repository", "fr.abes.sudoc.entity"})
public class BaseXMLOracleConfig  {

}
