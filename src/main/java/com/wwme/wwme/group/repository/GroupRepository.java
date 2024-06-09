package com.wwme.wwme.group.repository;

import com.wwme.wwme.group.domain.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface GroupRepository extends JpaRepository<Group, Long> {
}
