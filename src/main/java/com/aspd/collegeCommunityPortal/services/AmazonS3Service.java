package com.aspd.collegeCommunityPortal.services;


import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public interface AmazonS3Service {
    public void uploadFile(String path,String filename,Optional<Map<String,String>> metadata,InputStream inputStream);
    public byte[] downloadFile(String path,String filename);
}
