package com.folioframe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class FolioframeApplication {

	public static void main(String[] args) {
		SpringApplication.run(FolioframeApplication.class, args);
	}

}
