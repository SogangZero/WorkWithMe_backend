package com.wwme.wwme.fileupload.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileServiceImpl implements FileService {

    private final AmazonS3 amazonS3;

    @Value("${s3.bucket.name}")
    private String bucketName;

    @Override
    public String uploadFile(String filename, MultipartFile multipartFile) {
        //upload to Server

        try {
            ObjectMetadata objectMetadata =  new ObjectMetadata();
            objectMetadata.setContentLength(multipartFile.getInputStream().available());
            amazonS3.putObject(bucketName,filename,multipartFile.getInputStream(),objectMetadata);

            String url = URLDecoder.decode(amazonS3.getUrl(bucketName,multipartFile.getOriginalFilename()).toString(),"utf-8");
            log.info("File Upload successful("+multipartFile.getSize()+" bytes)" +", URL : " + url);
            return url;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage() + " in function uploadFile");
        }
    }

    @Override
    public File downloadFile(String foldername, String filenames) {
        return null;
    }
}
