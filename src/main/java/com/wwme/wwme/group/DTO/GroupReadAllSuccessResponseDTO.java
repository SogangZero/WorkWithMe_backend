package com.wwme.wwme.group.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class GroupReadAllSuccessResponseDTO {
    @JsonProperty("group_list")
    List<GroupDTO> groupDTOList = new ArrayList<>();

    @Data
    public static class GroupDTO {
        @JsonProperty("group_id")
        long groupId;

        @JsonProperty("group_name")
        String groupName;

        @JsonProperty("group_color")
        String groupColor;

        @JsonProperty("num_people")
        int numPeople;
    }
}
