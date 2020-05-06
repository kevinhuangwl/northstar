package tech.xuanwu.northstar.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("tech.xuanwu.northstar")
public class NorthstarApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(NorthstarApplication.class, args);
	}

}
