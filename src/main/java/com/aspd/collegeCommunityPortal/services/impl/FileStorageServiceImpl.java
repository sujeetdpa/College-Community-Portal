package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.services.AmazonS3Service;
import com.aspd.collegeCommunityPortal.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Autowired
    private AmazonS3Service amazonS3Service;

    @Override
    public void uploadProfileImage(String path, String filename, MultipartFile file){

    }

    @Override
    public void uploadImage(String path, String filename, MultipartFile file) {

    }

    @Override
    public void uploadDocument(String path, String filename, MultipartFile file) {

    }
}
