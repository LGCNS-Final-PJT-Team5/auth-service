package com.modive.authservice.dto;

import lombok.Data;

@Data
public class CarNumberRequest {
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}