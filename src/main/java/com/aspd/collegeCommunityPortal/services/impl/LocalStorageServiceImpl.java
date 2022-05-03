package com.aspd.collegeCommunityPortal.services.impl;

import com.aspd.collegeCommunityPortal.services.LocalStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class LocalStorageServiceImpl implements LocalStorageService {
    public static final String UPLOAD_DIR = System.getProperty("user.home").concat("/");

    @Override
    public String upload(List<MultipartFile> files) throws IOException {
        FileOutputStream fos;
        for (MultipartFile file : files) {
            File f = new File(UPLOAD_DIR);
            f.mkdirs();
            String path = UPLOAD_DIR + File.separator + file.getOriginalFilename();
            System.out.println("Path : " + path);
            fos = new FileOutputStream(path);
            fos.write(file.getBytes());
        }
        return "File uploaded";
    }

    public byte[] download(String path) throws IOException {
        return Files.readAllBytes(Path.of(path));
    }

    public Boolean uploadFile(String path, String filename, Optional<Map<String, String>> metadata, InputStream inputStream) throws IOException {
//        String newPath=UPLOAD_DIR+path;
//        File f=new File(newPath);
//        f.mkdirs();
//        String url=newPath+File.separator+filename;
//        FileOutputStream fos=new FileOutputStream(url);
//        fos.write(inputStream.readAllBytes());
//        fos.flush();

        String absPath = UPLOAD_DIR + path;
        Path path1 = Paths.get(absPath).toAbsolutePath();
        Files.createDirectories(path1);
        Path filePath = Paths.get(absPath + File.separator + filename).toAbsolutePath();
        Files.createFile(filePath);
        Files.write(filePath, inputStream.readAllBytes());
        return true;
    }

    public byte[] downloadFile(String path, String filename) throws IOException {
        String filePath = UPLOAD_DIR + path + File.separator + filename;
        return Files.readAllBytes(Path.of(filePath));
    }
}
