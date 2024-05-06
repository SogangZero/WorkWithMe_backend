package com.wwme.wwme.group.domain;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class Color {
    private short red;
    private short green;
    private short blue;
}
