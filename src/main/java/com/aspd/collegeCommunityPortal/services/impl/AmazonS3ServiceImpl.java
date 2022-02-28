package com.aspd.collegeCommunityPortal.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.aspd.collegeCommunityPortal.services.AmazonS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
public class AmazonS3ServiceImpl implements AmazonS3Service {

    @Autowired
    private AmazonS3 amazonS3;

    public void uploadFile(String path, String filename, Optional<Map<String,String>> metadata, InputStream inputStream){
        ObjectMetadata objectMetadata=new ObjectMetadata();
        metadata.ifPresent(map->{
            map.forEach(objectMetadata::addUserMetadata);
        });
        try{
            amazonS3.putObject(path,filename,inputStream,objectMetadata);
        }
        catch (AmazonServiceException e){
            throw new IllegalStateException("Failed to upload file",e);
        }
    }
    public byte[] downloadFile(String path,String filename){
        try {
            S3Object s3Object = amazonS3.getObject(path,filename);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        }
        catch (AmazonServiceException | IOException e){
            throw new IllegalStateException("Failed to download file",e);
        }

    }
}
