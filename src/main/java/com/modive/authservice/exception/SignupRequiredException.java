package com.modive.authservice.exception;

public class SignupRequiredException extends RuntimeException {

    public SignupRequiredException() {
        super("Signup required");
    }
}
