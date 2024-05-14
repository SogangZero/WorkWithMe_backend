package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.user.domain.User;

public interface GroupInvitationService {
    /**
     * Creates a group invitation entity given a group.
     * Generates a random code, and returns it.
     * @param group group of invitation to create
     * @return group invitation code
     */
    String createGroupInvitation(Group group);
    Group acceptInvitation(String code, User user, String color);
}
