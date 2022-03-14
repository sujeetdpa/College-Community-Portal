package com.aspd.collegeCommunityPortal.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class BucketName {
    @Value("${cloud.aws.bucket.ccp-bucket-name}")
    private String ccpBucketName;
}
