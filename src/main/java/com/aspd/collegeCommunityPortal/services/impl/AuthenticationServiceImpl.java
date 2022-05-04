package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.beans.request.AuthenticationRequest;
import com.aspd.collegeCommunityPortal.beans.request.ForgotPasswordRequest;
import com.aspd.collegeCommunityPortal.beans.request.RegisterRequest;
import com.aspd.collegeCommunityPortal.beans.request.UpdatePasswordRequest;
import com.aspd.collegeCommunityPortal.beans.response.AuthenticationResponse;
import com.aspd.collegeCommunityPortal.beans.response.SignUpResponse;
import com.aspd.collegeCommunityPortal.beans.response.UserResponseView;
import com.aspd.collegeCommunityPortal.model.*;
import com.aspd.collegeCommunityPortal.repositories.ConfirmationTokenRepository;
import com.aspd.collegeCommunityPortal.repositories.RoleRepository;
import com.aspd.collegeCommunityPortal.repositories.UserRepository;
import com.aspd.collegeCommunityPortal.services.AuthenticationService;
import com.aspd.collegeCommunityPortal.services.EmailService;
import com.aspd.collegeCommunityPortal.services.UserService;
import com.aspd.collegeCommunityPortal.util.JwtUtil;
import com.aspd.collegeCommunityPortal.util.TimeUtil;
import com.aspd.collegeCommunityPortal.util.UserUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("${server.domain}")
    private String serverDomain;

    @LocalServerPort
    private int port;

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
    @Autowired
    private EmailService emailService;
    @Autowired
    private ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    private TimeUtil timeUtil;

    @Override
    public AuthenticationResponse login(AuthenticationRequest request) {
        AuthenticationResponse response = null;
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        if (authenticate.isAuthenticated()) {
            final UserDetails userDetails = (UserDetails) authenticate.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);
            UserPrincipal userPrincipal = (UserPrincipal) userDetails;

            UserResponseView view = new UserResponseView();
            User user = userPrincipal.getUser();
            Optional.ofNullable(user.getId()).ifPresent(view::setId);
            Optional.ofNullable(user.getFullName()).ifPresent(view::setFullName);
            Optional.ofNullable(user.getUsername()).ifPresent(view::setUsername);
            Optional.ofNullable(user.getDob()).ifPresent(view::setDob);
            Optional.ofNullable(user.getUniversityId()).ifPresent(view::setUniversityId);
            Optional.ofNullable(user.getProfileImageId()).ifPresent(view::setProfileImageId);
            //Optional.ofNullable(user.getRoles()).map(roles -> roles.stream().map(Role::getName).collect(Collectors.toList())).ifPresent(view::setRole);
            response = new AuthenticationResponse();
            response.setAccess_token(token);
            response.setUserResponseView(view);
        }
        return response;
    }

    @Override
    public SignUpResponse signUp(RegisterRequest request) {
        User user = new User();
        if (!userUtil.validateUsername(request.getUsername())) {
            throw new IllegalStateException("Invalid username");
        }
        if (!request.getPassword().equals(request.getCnfPassword())) {
            throw new IllegalStateException("Password Mismatch");
        }
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already taken.");
        }
        if (userRepository.findByMobileNo(request.getMobileNo()).isPresent()) {
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
        user.setIsActive(false);
        user.setIsNotLocked(true);
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new IllegalArgumentException("Not able to find role"));
        user.getRoles().add(role);

        String ip;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("Failed to register please try again");
        }
        User savedUser = userRepository.save(user);

        ConfirmationToken confirmationToken = new ConfirmationToken();
        confirmationToken.setCreatedAt(LocalDateTime.now());
        String token = UUID.randomUUID().toString();
        confirmationToken.setToken(token);
        confirmationToken.setExpiresAt(LocalDateTime.now().plusDays(1));
        confirmationToken.setUser(savedUser);
        confirmationTokenRepository.save(confirmationToken);

        String link = "http://" + ip + ":" + port + "/auth/activate/account?token=" + token;
        emailService.sendActivationLinkEmail(savedUser.getFullName(), savedUser.getUsername(), link,timeUtil.getLastLoginTimestamp(confirmationToken.getExpiresAt()));
        emailService.sendRegistrationEmail(savedUser.getFirstName(), savedUser.getUsername(), request.getPassword());

        SignUpResponse response = new SignUpResponse();
        Optional.ofNullable(savedUser.getId()).ifPresent(response::setId);
        Optional.ofNullable(savedUser.getUsername()).ifPresent(response::setUsername);
        Optional.ofNullable(savedUser.getFullName()).ifPresent(response::setFullName);
        return response;
    }

    @Override
    public String updatePassword(UpdatePasswordRequest request) {
        if (request == null || request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty() || request.getCurrentPassword().isBlank()
                || request.getNewPassword() == null || request.getNewPassword().isEmpty() || request.getNewPassword().isBlank()
                || request.getCnfNewPassword() == null || request.getCnfNewPassword().isEmpty() || request.getCnfNewPassword().isBlank()) {
            throw new IllegalStateException("Fields cannot be empty");
        }
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userPrincipal.getUser();
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Incorrect Current Password.");
        }
        if (!request.getNewPassword().equals(request.getCnfNewPassword())) {
            throw new IllegalStateException("Password Mismatch");
        }

        user.setPassword(passwordEncoder.encode(request.getCnfNewPassword()));
        userRepository.save(user);
        emailService.sendPasswordChangeEmail(user.getFirstName(), user.getUsername(), request.getCnfNewPassword());
        return "Password Updated Successfully";
    }

    @Override
    public String forgotPassword(ForgotPasswordRequest request) {
        Optional<User> optionalUser = userRepository.findByUsername(request.getUsername());
        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("Incorrect Username / DOB");
        }
        User user = optionalUser.get();
        if (!user.getDob().equals(request.getDob())) {
            throw new IllegalStateException("Incorrect Username / DOB");
        }
        String password = RandomStringUtils.randomAlphanumeric(10);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        emailService.sendForgotPasswordEmail(user.getFirstName(), user.getUsername(), password);
        return "Password is sent on the username : " + request.getUsername();
    }

    @Override
    public String activateAccount(String token) throws UnknownHostException {
        String ip=InetAddress.getLocalHost().getHostAddress();
        String loginButton="<a href='http://"+ip+":"+port+"/' class='btn btn-primary'>Login</a>";
        Optional<ConfirmationToken> optionalConfirmationToken = confirmationTokenRepository.findByToken(token);
        if (optionalConfirmationToken.isEmpty()) {
            return "<h1 style='color:red'>Invalid activation link</h1>";
        }
        ConfirmationToken confirmationToken = optionalConfirmationToken.get();
        if (confirmationToken.getConfirmedAt() != null) {
            return "<h1 style='color:green'>Account already activated</h1>"+loginButton;
        }
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())){
            return "<h1 style='color:red'>Link expired, Please contact administrator";
        }

        User user = confirmationToken.getUser();
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationTokenRepository.save(confirmationToken);
        user.setIsActive(true);
        userRepository.save(user);
        return "<h1 style='color:green'>Account activated successfully</h1>"+loginButton;
    }
}
