package com.wwme.wwme.task.repository;

import com.wwme.wwme.task.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Tag_Repository extends JpaRepository<Tag,Long> {

}
