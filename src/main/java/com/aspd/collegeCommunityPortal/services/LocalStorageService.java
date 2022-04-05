package com.aspd.collegeCommunityPortal.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface LocalStorageService {
    String upload(List<MultipartFile> file) throws IOException;
    public byte[] download(String path) throws IOException;

    public byte[] downloadFile(String path,String filename) throws IOException;
    public Boolean uploadFile(String path, String filename, Optional<Map<String,String>> metadata, InputStream inputStream) throws IOException;
}
