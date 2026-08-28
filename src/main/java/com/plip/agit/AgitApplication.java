package com.plip.agit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AgitApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgitApplication.class, args);
	}

}
