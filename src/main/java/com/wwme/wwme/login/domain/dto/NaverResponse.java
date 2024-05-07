package com.wwme.wwme.login.domain.dto;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.Map;

@ToString
@RequiredArgsConstructor
public class NaverResponse implements OAuth2Response {
    private final Map<String, Object> attribute;

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getName() {
        return attribute.get("name").toString();
    }
}
