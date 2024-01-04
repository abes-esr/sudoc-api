package fr.abes.convergence.kbartws.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI OpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Sudoc Api")
                        .description("Service web RESTful sur les données KBart et interrogeant le Sudoc via l'API AccesCbs")
                        .version(this.getClass().getPackage().getImplementationVersion())
                        .contact(new Contact().url("https://github.com/abes-esr/convergence-webservices").name("Abes").email("scod@abes.fr")));
    }
}
