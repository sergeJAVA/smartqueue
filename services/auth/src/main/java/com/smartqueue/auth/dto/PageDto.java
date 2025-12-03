package com.smartqueue.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageDto<T> {

    @Builder.Default
    private List<T> content = new ArrayList<>();

    private int number;

    private int size;

    private int numberOfElements;

    private int totalPages;

    private long totalElements;

    private boolean last;

    private boolean first;

}
