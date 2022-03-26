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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
@EnableJpaRepositories

public class CollegeCommunityPortalApplication {
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(CollegeCommunityPortalApplication.class, args);
	}

	public void initialData(){
		Role role=new Role();role.setName("ROLE_ADMIN");
		Role role1=new Role();role1.setName("ROLE_USER");
		roleRepository.saveAll(Arrays.asList(role,role1));

		User user=new User();
		user.setFirstName("Helo");
		user.setLastName("Fox");
		user.setUsername("hello");
		user.setPassword(new BCryptPasswordEncoder().encode("helo"));
		user.setRoles(Arrays.asList(role));
		user.setEmail("hello@gmail.com");
		user.setGender(Gender.MALE);
		user.setIsActive(true);
		user.setIsNotBlocked(true);
		user.setUserCreationTimestamp(LocalDateTime.now());

		System.out.println(userRepository.save(user));


	}
}
