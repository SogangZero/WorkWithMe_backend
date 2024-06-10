package com.wwme.wwme.task.domain.DTO.converter;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class UniversalTypeConverter {

    public static <T, U> T convert(U source, Class<T> targetType) throws Exception {
        T target = targetType.getDeclaredConstructor().newInstance();
        Field[] sourceFields = source.getClass().getDeclaredFields();
        Field[] targetFields = targetType.getDeclaredFields();

        for (Field sourceField : sourceFields) {
            sourceField.setAccessible(true);
            for (Field targetField : targetFields) {
                if (sourceField.getName().equals(targetField.getName()) && sourceField.getType().equals(targetField.getType())) {
                    targetField.setAccessible(true);
                    targetField.set(target, sourceField.get(source));
                }
            }
        }
        return target;
    }
}
