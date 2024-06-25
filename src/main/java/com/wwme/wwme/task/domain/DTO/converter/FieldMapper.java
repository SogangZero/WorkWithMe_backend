package com.wwme.wwme.task.domain.DTO.converter;

@FunctionalInterface
public interface FieldMapper {
    Object map(Object source);
}
