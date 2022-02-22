package com.aspd.collegeCommunityPortal.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloud.aws.bucket")
@Data
public class BucketName {
    private String ccpBucketName;
}
