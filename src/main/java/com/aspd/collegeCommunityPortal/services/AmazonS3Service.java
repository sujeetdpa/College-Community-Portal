package com.aspd.collegeCommunityPortal.services;


import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public interface AmazonS3Service {
    public void uploadFile(PutObjectRequest putObjectRequest);
    public byte[] downloadFile(GetObjectRequest getObjectRequest);
}
