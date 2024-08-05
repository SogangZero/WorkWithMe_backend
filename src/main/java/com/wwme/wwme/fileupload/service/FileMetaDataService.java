package com.wwme.wwme.fileupload.service;

import com.wwme.wwme.fileupload.domain.DTO.FileMetaDataReturnDTO;
import com.wwme.wwme.fileupload.domain.FileMetaData;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.task.domain.Task;

import java.util.List;

public interface FileMetaDataService {

    /**
     * Create and save a fileMetaData entity.
     * @param task
     * @param group
     * @param originalFileName
     * @return
     */
    FileMetaData createAndSaveFileMetaData(Task task, Group group, String originalFileName, String url, String savedFileName);

    /**
     * Create and save a fileMetaData entity without task
     * @param group
     * @param originalFileName
     * @return
     */
    FileMetaData createAndSaveFileMetaData(Group group, String originalFileName, String url, String savedFileName);

    /**
     * Create a unique file name for saving the file to S3 database.
     * Must have a prefix of "groupID/taskID/"
     * @param group
     * @param task
     * @return
     */
    String createSavingFileName(Group group, Task task);


    /**
     * Create a unique file name for saving the file to S3 database.
     * Must have a prefix of "groupID/"
     * @param group
     * @return
     */
    String createSavingFileName(Group group);

    /**
     * get MetaData information List of saved files.
     * Does not access S3 server for verification of files
     * @param task
     * @return
     */
    List<FileMetaDataReturnDTO> getFileMetaDataFromTask(Task task);

    /**
     * get MetaData information List of all saved files of specific group.
     * Does not access S3 server for verification of files
     * @param group
     * @return
     */
    List<FileMetaDataReturnDTO> getFileMetaDataFromGroup(Group group);

    FileMetaData getMetaDataByID(Long fileId);
}
