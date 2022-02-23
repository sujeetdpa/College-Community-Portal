package com.aspd.collegeCommunityPortal.services;


import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

public interface FileStorageService {
    public void uploadFile(String path, String filename, Optional<Map<String,String>> optionalMetadata, InputStream inputStream);
    public byte[] downloadFile(String path, String key);
}
