package com.wwme.wwme.fileupload.repository;

import com.wwme.wwme.fileupload.domain.FileMetaData;
import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetaDataRepository extends JpaRepository<FileMetaData,Long> {
    List<FileMetaData> findByTask(Task task);
    List<FileMetaData> findByGroup(Group group);
}
