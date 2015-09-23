package demo;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {
	
	@Bean
	CommandLineRunner dummy(ReservationRepository r){
		return strings -> {
			Arrays.asList("jakub,thomas,peter,josh,juergen".split(","));
		};
	}

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }
}
