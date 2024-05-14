package com.wwme.wwme.group.service;

import com.wwme.wwme.group.domain.Group;
import com.wwme.wwme.group.domain.GroupInvitation;
import com.wwme.wwme.group.domain.UserGroup;
import com.wwme.wwme.group.repository.GroupInvitationRepository;
import com.wwme.wwme.group.repository.GroupRepository;
import com.wwme.wwme.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class GroupInvitationServiceImpl implements GroupInvitationService {
    private final GroupInvitationRepository groupInvitationRepository;
    private final UserGroupService userGroupService;

    @Override
    public String createGroupInvitation(Group group) {
        String candidate = "abcdefghijklmnopqrstuvwxyz1234567890";
        int codeLength = 10;

        String randomCode = generateRandomUniqueString(candidate, codeLength);
        GroupInvitation groupInvitation = new GroupInvitation();
        groupInvitation.setGroup(group);
        groupInvitation.setCode(randomCode);

        groupInvitationRepository.save(groupInvitation);

        return randomCode;
    }

    @Override
    public Group acceptInvitation(String code, User user, String color) {
        GroupInvitation groupInvitation = groupInvitationRepository.findByCode(code).orElseThrow();
        Group group = groupInvitation.getGroup();

        UserGroup userGroup = userGroupService.addUserToGroupWithColor(group, user, color);
        return userGroup.getGroup();
    }

    private String generateRandomUniqueString(String candidate, int len) {
        while(true){
            String generatedString = generateRandomString(candidate, len);
            if(groupInvitationRepository.findByCode(generatedString).isEmpty()){
                return generatedString;
            }
        }
    }

    private String generateRandomString(String candidate, int len) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0; i<len; i++){
            int idx = random.nextInt(candidate.length());
            stringBuilder.append(candidate.charAt(idx));
        }
        return stringBuilder.toString();
    }
}