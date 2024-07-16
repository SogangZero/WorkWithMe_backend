package com.wwme.wwme.notification.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SetRegistrationTokenRequestDTO {
    @JsonProperty("registration_token")
    String registrationToken;
}
