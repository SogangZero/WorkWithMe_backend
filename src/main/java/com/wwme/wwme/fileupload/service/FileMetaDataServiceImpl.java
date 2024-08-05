package com.wwme.wwme.fileupload.service;

import com.wwme.wwme.fileupload.domain.DTO.FileMetaDataReturnDTO;
import com.wwme.wwme.fileupload.domain.FileMetaData;
import com.wwme.wwme.fileupload.repository.FileMetaDataRepository;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.task.domain.Task;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileMetaDataServiceImpl implements FileMetaDataService {

    private final FileMetaDataRepository fileMetaDataRepository;
    @Override
    public FileMetaData createAndSaveFileMetaData(Task task, Group group, String originalFileName, String url, String savedFileName) {

        FileMetaData fileMetaData = FileMetaData.builder()
                .task(task)
                .group(group)
                .originalFileName(originalFileName)
                .url(url)
                .savedFileName(savedFileName)
                .build();

        return fileMetaDataRepository.save(fileMetaData);
    }

    @Override
    public FileMetaData createAndSaveFileMetaData(Group group, String originalFileName, String url, String savedFileName) {
        FileMetaData fileMetaData = FileMetaData.builderWithoutTask()
                .group(group)
                .originalFileName(originalFileName)
                .url(url)
                .savedFileName(savedFileName)
                .build();
        return fileMetaDataRepository.save(fileMetaData);
    }

    @Override
    public String createSavingFileName(Group group, Task task) {

        UUID uuid = UUID.randomUUID();
        return String.format("%s/%s/%s",group.getId(),task.getId(), uuid);
    }

    @Override
    public String createSavingFileName(Group group) {
        UUID uuid = UUID.randomUUID();
        return String.format("%s/%s",group.getId(),uuid);
    }

    @Override
    public List<FileMetaDataReturnDTO> getFileMetaDataFromTask(Task task) {
        List<FileMetaData> fileMetaDatas = fileMetaDataRepository.findByTask(task);
        return metadataToDTOs(fileMetaDatas);
    }

    @Override
    public List<FileMetaDataReturnDTO> getFileMetaDataFromGroup(Group group) {
        List<FileMetaData> list = fileMetaDataRepository.findByGroup(group);
        return metadataToDTOs(list);
    }

    @Override
    public FileMetaData getMetaDataByID(Long fileId) {
        return fileMetaDataRepository.findById(fileId).orElseThrow(()->new NoSuchElementException(
                "Could not find Metadata with the following ID : " + fileId
        ));

    }

    private List<FileMetaDataReturnDTO> metadataToDTOs(List<FileMetaData> list) {
        List<FileMetaDataReturnDTO> fileMetaDataReturnDTOS = new ArrayList<>();

        for (FileMetaData fileMetaData : list){
            FileMetaDataReturnDTO dto = FileMetaDataReturnDTO.builder()
                    .FileName(fileMetaData.getOriginalFileName())
                    .Id(fileMetaData.getId())
                    .build();
            fileMetaDataReturnDTOS.add(dto);
        }
        return fileMetaDataReturnDTOS;
    }

}
