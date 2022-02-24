package com.aspd.collegeCommunityPortal.services.impl;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.IOUtils;
import com.aspd.collegeCommunityPortal.services.AmazonS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class AmazonS3ServiceImpl implements AmazonS3Service {

    @Autowired
    private AmazonS3 amazonS3;

    public void uploadFile(PutObjectRequest putObjectRequest){
        try{
            amazonS3.putObject(putObjectRequest);
        }
        catch (AmazonServiceException e){
            throw new IllegalStateException("Failed to upload file",e);  //TODO Implement Custom exception
        }
    }
    public byte[] downloadFile(GetObjectRequest getObjectRequest){
        try {
            S3Object s3Object = amazonS3.getObject(getObjectRequest);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        }
        catch (AmazonServiceException | IOException e){
            throw new IllegalStateException("Failed to download file",e); //TODO Implement Custom Exception
        }

    }
}
