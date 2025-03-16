package assignment;

import assignment.config.security.JwtConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class SpringBootAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAssignmentApplication.class, args);
    }

}
