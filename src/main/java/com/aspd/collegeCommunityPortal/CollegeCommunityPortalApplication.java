package com.aspd.collegeCommunityPortal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class CollegeCommunityPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollegeCommunityPortalApplication.class, args);
	}

}
