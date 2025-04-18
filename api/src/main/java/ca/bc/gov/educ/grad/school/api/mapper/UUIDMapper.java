package ca.bc.gov.educ.grad.school.api.mapper;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class UUIDMapper {

    public UUID map(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return UUID.fromString(value);
    }

    public String map(UUID value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
