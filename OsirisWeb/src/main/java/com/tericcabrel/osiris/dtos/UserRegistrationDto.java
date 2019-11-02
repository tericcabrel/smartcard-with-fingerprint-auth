package com.tericcabrel.osiris.dtos;

import javax.validation.constraints.*;

public class UserRegistrationDto {
    @Size(min = 10, max = 10, message = "Must be at least 10 characters")
    private String uid;

    @NotBlank(message = "The name is required")
    private String name;

    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Invalid date of birth")
    private String birthDate;

    // @NotBlank(message = "The name is required")
    private String finger;

    public String getUid() {
        return uid;
    }

    public UserRegistrationDto setUid(String uid) {
        this.uid = uid;
        return this;
    }

    public String getName() {
        return name;
    }

    public UserRegistrationDto setName(String name) {
        this.name = name;
        return this;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public UserRegistrationDto setBirthDate(String birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public String getFinger() {
        return finger;
    }

    public UserRegistrationDto setFinger(String finger) {
        this.finger = finger;
        return this;
    }
}
