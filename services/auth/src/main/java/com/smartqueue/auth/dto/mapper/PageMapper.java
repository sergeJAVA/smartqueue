package com.smartqueue.auth.dto.mapper;

import com.smartqueue.auth.dto.PageDto;
import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;

@UtilityClass
public class PageMapper {

    public static <T> PageDto<T> toDto(Page<T> page) {
        return PageDto.<T>builder()
                .content(page.getContent())
                .size(page.getSize())
                .number(page.getNumber())
                .first(page.isFirst())
                .last(page.isLast())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .numberOfElements(page.getNumberOfElements())
                .build();
    }

}
