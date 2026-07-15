package com.samtar.userservice.constants;

public final class RegexConstant {
    private RegexConstant() {}

    public static final String EMAIL =
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    public static final String PHONE =
            "^[6-9]\\d{9}$";

    public static final String USERNAME =
            "^(?=.{3,30}$)[A-Za-z0-9._]+$";

    public static final String PASSWORD =
            "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
}
