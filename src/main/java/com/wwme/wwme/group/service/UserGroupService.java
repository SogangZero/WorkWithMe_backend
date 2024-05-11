package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.user.domain.User;

import java.util.NoSuchElementException;

public interface UserGroupService {
    UserGroup getUserGroupByIdAndUser(long groupId, User user) throws NoSuchElementException, IllegalAccessException;
}
