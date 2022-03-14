package com.aspd.collegeCommunityPortal.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@Data
public class JWTConfig {
    @Value("${security.jwt.secret-key}")
    private String secretKey;
}
