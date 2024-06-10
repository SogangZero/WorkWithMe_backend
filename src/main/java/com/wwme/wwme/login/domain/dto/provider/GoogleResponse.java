package com.wwme.wwme.login.domain.dto.provider;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

@ToString
@RequiredArgsConstructor
public class GoogleResponse implements OAuth2Response {
    private final Map<String, Object> attribute;

    @Override
    public String getProvider() {
        return "google";
    }

    @Override
    public String getProviderId() {
        return attribute.get("sub").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }

}
