package com.smartqueue.auth.dto.request;

import com.smartqueue.auth.dto.Pagination;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchUsersRequest {

    private String username;

    private List<String> roles;

    private Pagination pagination;

}
