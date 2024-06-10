package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.GroupInvitation;
import com.wwme.wwme.group.repository.GroupInvitationRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupInvitationServiceImpl implements GroupInvitationService {
    private final GroupInvitationRepository groupInvitationRepository;
    private final UserGroupService userGroupService;

    @Override
    public String createGroupInvitation(Group group) {
        final String candidate = "abcdefghijklmnopqrstuvwxyz1234567890";
        final int codeLength = 10;

        String randomCode = generateRandomUniqueString(candidate, codeLength);
        GroupInvitation groupInvitation = new GroupInvitation();
        groupInvitation.setGroup(group);
        groupInvitation.setCode(randomCode);

        groupInvitationRepository.save(groupInvitation);

        return randomCode;
    }

    @Override
    public Group acceptInvitation(String code, User user, String color) {
        var groupInvitation = groupInvitationRepository.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException("Couldn't find a groupInvitation with the given code"));

        var group = groupInvitation.getGroup();

        var userGroup = userGroupService.addUserToGroupWithColor(group, user, color);
        return userGroup.getGroup();
    }

    private String generateRandomUniqueString(String candidate, int len) {
        while (true) {
            String generatedString = generateRandomString(candidate, len);
            if (groupInvitationRepository.findByCode(generatedString).isEmpty()) {
                return generatedString;
            }
        }
    }

    private String generateRandomString(String candidate, int len) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < len; i++) {
            int idx = random.nextInt(candidate.length());
            stringBuilder.append(candidate.charAt(idx));
        }
        return stringBuilder.toString();
    }
}
