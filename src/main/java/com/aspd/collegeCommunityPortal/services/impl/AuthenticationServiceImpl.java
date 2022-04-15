package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.request.AuthenticationRequest;
import com.aspd.collegeCommunityPortal.beans.request.RegisterRequest;
import com.aspd.collegeCommunityPortal.beans.response.AuthenticationResponse;
import com.aspd.collegeCommunityPortal.beans.response.SignUpResponse;
import com.aspd.collegeCommunityPortal.model.Gender;
import com.aspd.collegeCommunityPortal.model.Role;
import com.aspd.collegeCommunityPortal.model.User;
import com.aspd.collegeCommunityPortal.repositories.RoleRepository;
import com.aspd.collegeCommunityPortal.repositories.UserRepository;
import com.aspd.collegeCommunityPortal.services.AuthenticationService;
import com.aspd.collegeCommunityPortal.services.UserService;
import com.aspd.collegeCommunityPortal.util.JwtUtil;
import com.aspd.collegeCommunityPortal.util.UserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserUtil userUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        AuthenticationResponse response=null;
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        if(authenticate.isAuthenticated()){
            final UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            response=new AuthenticationResponse();
            response.setAccess_token(token);
        }
        return response;
    }

    @Override
    public SignUpResponse signUp(RegisterRequest request) {
        User user=new User();
        if (!userUtil.validateUsername(request.getUsername())){
            throw new IllegalStateException("Invalid username"); //TODO handle exception
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new IllegalStateException("Username already taken.");
        }
        if (userRepository.findByMobileNo(request.getMobileNo()).isPresent()){
            throw new IllegalStateException("Mobile Number already taken.");
        }
        Optional.ofNullable(request.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(request.getLastName()).ifPresent(user::setLastName);
        Optional.ofNullable(request.getUsername()).ifPresent(user::setUsername);
        Optional.ofNullable(request.getDob()).ifPresent(user::setDob);
        Optional.ofNullable(request.getGender()).map(Gender::valueOf).ifPresent(user::setGender);
        Optional.ofNullable(request.getMobileNo()).ifPresent(user::setMobileNo);
        Optional.ofNullable(request.getPassword()).map(passwordEncoder::encode).ifPresent(user::setPassword);
        Optional.ofNullable(request.getUsername()).map(userUtil::getUniversityId).ifPresent(user::setUniversityId);
        Optional.ofNullable(LocalDateTime.now()).ifPresent(user::setUserCreationTimestamp);
        user.setIsActive(true);  // TODO implement later
        user.setIsNotLocked(true); //TODO implement later
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new IllegalArgumentException("Not able to find role"));//TODO handle exception
        user.getRoles().add(role);
        User savedUser = userRepository.save(user);
        SignUpResponse response=new SignUpResponse();
        Optional.ofNullable(savedUser.getId()).ifPresent(response::setId);
        Optional.ofNullable(savedUser.getUsername()).ifPresent(response::setUsername);
        Optional.ofNullable(savedUser.getFullName()).ifPresent(response::setFullName);
        return response;
    }
}
