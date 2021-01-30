package com.u4.ppmtool.exceptions;

public class UserNotUniqueExceptionResponse {
    private String userNotUnique;

    public UserNotUniqueExceptionResponse(String userNotUnique) {
        this.userNotUnique = userNotUnique;
    }

    public String getUserNotUnique() {
        return userNotUnique;
    }

    public void setUserNotUnique(String userNotUnique) {
        this.userNotUnique = userNotUnique;
    }
}
