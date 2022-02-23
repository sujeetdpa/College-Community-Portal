package com.aspd.collegeCommunityPortal.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.aspd.collegeCommunityPortal.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class FileStorageServiceImpl implements FileStorageService {
    @Autowired
    private AmazonS3 amazonS3;

    public void uploadFile(String path, String filename, Optional<Map<String,String>> optionalMetadata, InputStream inputStream){
        ObjectMetadata objectMetadata=new ObjectMetadata();
        optionalMetadata.ifPresent(s->{
            if (!s.isEmpty()){
                s.forEach(objectMetadata::addUserMetadata);
            }
        });
        try{
            amazonS3.putObject(path,filename,inputStream,objectMetadata);
        }
        catch (AmazonServiceException e){
            throw new IllegalStateException("Failed to upload file",e);  //TODO Implement Custom exception
        }
    }
    public byte[] downloadFile(String path, String filename){
        try {
            S3Object s3Object = amazonS3.getObject(path, filename);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        }
        catch (AmazonServiceException | IOException e){
            throw new IllegalStateException("Failed to download file",e); //TODO Implement Custom Exception
        }

    }
}
