package com.modive.authservice.response;

import com.modive.authservice.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {

    private int length;
    private List<String> usernicknames;

    public UserListResponse(List<String> usernicknames) {
        this.length = usernicknames.size();
        this.usernicknames = usernicknames;
    }

    public static UserListResponse of(List<String> usernicknames) {
        return UserListResponse.builder()
                .length(usernicknames.size())
                .usernicknames(usernicknames)
                .build();

    }
}
