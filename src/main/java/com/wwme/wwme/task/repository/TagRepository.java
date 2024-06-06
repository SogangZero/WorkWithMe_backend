package com.wwme.wwme.task.repository;

import com.wwme.wwme.task.domain.DTO.TagDTO;
import com.wwme.wwme.task.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag,Long> {

    List<Tag> findAllByGroupId(Long groupId);

}
