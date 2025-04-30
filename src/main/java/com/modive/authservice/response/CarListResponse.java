package com.modive.authservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarListResponse {

    private int length;
    private List<String> numbers;

    public CarListResponse(List<String> usernicknames) {
        this.length = usernicknames.size();
        this.numbers = usernicknames;
    }

    public static CarListResponse of(List<String> numbers) {
        return CarListResponse.builder()
                .length(numbers.size())
                .numbers(numbers)
                .build();

    }
}
