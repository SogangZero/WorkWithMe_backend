package com.wwme.wwme.fileupload.controller;

import com.wwme.wwme.fileupload.domain.DTO.FileMetaDataReturnDTO;
import com.wwme.wwme.fileupload.domain.DTO.UploadFileWithGroupReturnDTO;
import com.wwme.wwme.fileupload.domain.FileMetaData;
import com.wwme.wwme.fileupload.service.FileMetaDataService;
import com.wwme.wwme.fileupload.service.FileMetaDataServiceImpl;
import com.wwme.wwme.fileupload.service.FileService;
import com.wwme.wwme.group.DTO.DataWrapDTO;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.service.GroupService;
import com.wwme.wwme.login.aop.Login;
import com.wwme.wwme.task.domain.DTO.sendDTO.ErrorResponseDTO;
import com.wwme.wwme.task.domain.Task;
import com.wwme.wwme.task.service.TaskCRUDService;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final FileMetaDataService fileMetaDataService;
    private final TaskCRUDService taskCRUDService;
    private final GroupService groupService;

    @PostMapping("/group")
    public ResponseEntity<?> uploadFileWithGroup(@Login User loginUser,
                                                 @RequestParam(name = "group_id") Long groupId,
                                                 @RequestParam(name = "file")MultipartFile file
                                                 ){
        try {
            //0. get group entity
            Group group = groupService.getGroupByID(groupId);
            //1. CreateSavingFileName
            String savingFileName = fileMetaDataService.createSavingFileName(group);
            //2. Save file to S3, get url.
            String url =  fileService.uploadFile(savingFileName,file);
            //3. create and save metadata.
            FileMetaData fileMetaData =  fileMetaDataService.createAndSaveFileMetaData(group,file.getOriginalFilename(),url,savingFileName);

            UploadFileWithGroupReturnDTO uploadFileWithGroupReturnDTO = UploadFileWithGroupReturnDTO.builder()
                    .filemetadata_id(fileMetaData.getId())
                    .build();
            return ResponseEntity.ok(new DataWrapDTO(uploadFileWithGroupReturnDTO));

        } catch (Exception e) {
            log.error("Upload file with group error :" + e.getMessage());
            return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }


    }

    @PostMapping("/task")
    public ResponseEntity<?> uploadFileWithTask(@Login User loginUser,
                                                @RequestParam(name = "task_id") Long taskId,
                                                @RequestParam("file") MultipartFile file){
        try {
            //0. get task entity
            Task task = taskCRUDService.getTaskByID(taskId);
            //1. get group entity
            Group group = task.getGroup();
            //2. CreateSavingFileName
            String savingFileName = fileMetaDataService.createSavingFileName(group,task);
            //3. save file to S3
            String url = fileService.uploadFile(savingFileName,file);
            //4. create and save metadata
            FileMetaData fileMetaData =  fileMetaDataService.createAndSaveFileMetaData(task,group,file.getOriginalFilename(),url,savingFileName);

            UploadFileWithGroupReturnDTO uploadFileWithGroupReturnDTO = UploadFileWithGroupReturnDTO.builder()
                    .filemetadata_id(fileMetaData.getId())
                    .build();
            return ResponseEntity.ok(new DataWrapDTO(uploadFileWithGroupReturnDTO));
        } catch (Exception e) {
            log.error("Upload file with task error :" + e.getMessage());
            return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/list/task")
    public ResponseEntity<?> getFileListByTask(@Login User loginUser,
                                                @RequestParam(name = "task_id") Long taskId){
        try {
            //1. get task information from taskId
            Task task = taskCRUDService.getTaskByID(taskId);
            //2. get List of Metadata from DB
            List<FileMetaDataReturnDTO> fileMetaDataReturnDTOS = fileMetaDataService.getFileMetaDataFromTask(task);
            //3. send List of Metadata to Frontend
            return ResponseEntity.ok(new DataWrapDTO(fileMetaDataReturnDTOS));
        } catch (Exception e) {
            log.error("getFileListByTask error :" + e.getMessage());
            return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/list/group")
    public ResponseEntity<?> getFileListByGroup(@RequestParam(name = "task_id") Long taskId){
        try {
            //1. get task and group information from taskId
            Task task = taskCRUDService.getTaskByID(taskId);
            Group group = task.getGroup();
            //2. get List of Metadata from DB
            List<FileMetaDataReturnDTO> fileMetaDataReturnDTOS = fileMetaDataService.getFileMetaDataFromGroup(group);
            //3. send List of Metadata to Frontend
            return ResponseEntity.ok(new DataWrapDTO(fileMetaDataReturnDTOS));
        } catch (Exception e) {
            log.error("getFileListByGroup error :" + e.getMessage());
            return new ResponseEntity<>(new ErrorResponseDTO(e.getMessage()), HttpStatus.BAD_REQUEST);
        }

    }

}
