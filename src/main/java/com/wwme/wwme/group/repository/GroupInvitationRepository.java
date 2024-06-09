package com.wwme.wwme.group.repository;

import com.wwme.wwme.group.domain.GroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupInvitationRepository extends JpaRepository<GroupInvitation, Long> {
    Optional<GroupInvitation> findByCode(String code);
    Optional<GroupInvitation> findByGroupId(long groupId);
}
