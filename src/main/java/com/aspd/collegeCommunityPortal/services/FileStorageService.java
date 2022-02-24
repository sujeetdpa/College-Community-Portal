package com.aspd.collegeCommunityPortal.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    public void uploadProfileImage(String path, String filename, MultipartFile file);
    public void uploadImage(String path,String filename,MultipartFile file);
    public void uploadDocument(String path,String filename,MultipartFile file);
}
