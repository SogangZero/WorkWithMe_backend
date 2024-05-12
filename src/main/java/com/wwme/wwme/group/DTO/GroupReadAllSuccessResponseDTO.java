package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GroupReadAllSuccessResponseDTO {
    boolean success;
    List<GroupDTO> groupDTOList = new ArrayList<>();

    @Data
    public static class GroupDTO {
        @JsonProperty("group_id")
        long groupId;

        @JsonProperty("group_name")
        String groupName;

        @JsonProperty("group_color")
        String groupColor;
    }
}
