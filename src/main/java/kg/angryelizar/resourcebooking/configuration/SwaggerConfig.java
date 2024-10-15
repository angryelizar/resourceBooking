package kg.angryelizar.resourcebooking.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI(@Value("${spring.application.name}") String applicationName,
                                 @Value("${spring.application.description}") String applicationDescription,
                                 @Value("${spring.application.version}") String applicationVersion) {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName)
                        .version(applicationVersion)
                        .description(applicationDescription)
                        .contact(
                                new Contact()
                                        .name("Elizar Konovalov")
                                        .email("conovalov.elizar@gmail.com")
                                        .url("https://github.com/angryelizar/")
                        )
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
