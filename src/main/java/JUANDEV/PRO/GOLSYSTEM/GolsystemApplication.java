package JUANDEV.PRO.GOLSYSTEM;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing //
public class GolsystemApplication {

	public static void main(String[] args) {

        SpringApplication.run(GolsystemApplication.class, args);
    }

}
