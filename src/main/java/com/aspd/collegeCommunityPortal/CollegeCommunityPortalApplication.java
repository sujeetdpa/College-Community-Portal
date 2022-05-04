package com.aspd.collegeCommunityPortal;

import com.aspd.collegeCommunityPortal.model.Gender;
import com.aspd.collegeCommunityPortal.model.Role;
import com.aspd.collegeCommunityPortal.model.User;
import com.aspd.collegeCommunityPortal.repositories.RoleRepository;
import com.aspd.collegeCommunityPortal.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
@EnableJpaRepositories
@EnableAsync
public class CollegeCommunityPortalApplication {
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(CollegeCommunityPortalApplication.class, args);
	}

	@PostConstruct
	public void initialData(){
		try {
			Role role = new Role();
			role.setName("ROLE_ADMIN");
			Role role1 = new Role();
			role1.setName("ROLE_USER");
			roleRepository.saveAll(Arrays.asList(role, role1));

			User user = new User();
			user.setFirstName("Admin");
			user.setUsername("admin@mmmut.ac.in");
			user.setUniversityId("admin");
			user.setPassword(new BCryptPasswordEncoder().encode("admin"));
			user.setRoles(Arrays.asList(role));
			user.setEmail("hello@gmail.com");
			user.setGender(Gender.MALE);
			user.setDob(LocalDate.of(2000, 01, 01));
			user.setMobileNo("2334455778");
			user.setIsActive(true);
			user.setIsNotLocked(true);
			user.setUserCreationTimestamp(LocalDateTime.now());
			System.out.println(userRepository.save(user));
		}catch (Exception e){
			System.out.println("ERROR: Initial Data already present");
		}
	}
}
