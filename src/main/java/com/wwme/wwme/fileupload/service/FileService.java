package com.wwme.wwme.fileupload.service;

import com.wwme.wwme.fileupload.domain.DTO.FileMetaDataReturnDTO;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.task.domain.Task;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileService {

    /**
     * Upload a file to the cloud.
     * @return
     */
    public String uploadFile(String filename, MultipartFile multipartFile);
    public File downloadFile(String foldername, String filenames);

}
