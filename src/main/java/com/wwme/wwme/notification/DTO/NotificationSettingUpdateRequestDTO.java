package com.wwme.wwme.notification.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Setter;

@Setter
public class NotificationSettingUpdateRequestDTO {
    @JsonProperty("on_due_date")
    boolean onDueDate;

    @JsonProperty("onMyTaskCreation")
    boolean onMyTaskCreation;

    @JsonProperty("onMyTaskChange")
    boolean onMyTaskChange;

    @JsonProperty("onGroupEntrance")
    boolean onGroupEntrance;

    public boolean onDueDate() {
        return onDueDate;
    }

    public boolean onMyTaskCreation() {
        return onMyTaskCreation;
    }

    public boolean onMyTaskChange() {
        return onMyTaskChange;
    }

    public boolean onGroupEntrance() {
        return onGroupEntrance;
    }
}
