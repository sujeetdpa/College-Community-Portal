package com.aspd.collegeCommunityPortal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableJpaRepositories

public class CollegeCommunityPortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(CollegeCommunityPortalApplication.class, args);
	}

}
