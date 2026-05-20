package cl.hitzone.ms_matches;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MsMatchesApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsMatchesApplication.class, args);
	}

}
