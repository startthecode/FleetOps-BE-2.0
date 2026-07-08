package com.samtar.userservice.constants;

public class MessageConstant {
    private MessageConstant() {}

    // Validation Messages
    public static final String REQUIRED = "This field is required.";
    public static final String INVALID_EMAIL = "Please enter a valid email address.";
    public static final String INVALID_PHONE = "Please enter a valid phone number.";
    public static final String INVALID_USERNAME = "Username format is invalid.";
    public static final String INVALID_PASSWORD = "Password does not meet security requirements.";

    // Success Messages
    public static final String USER_CREATED = "User created successfully.";
    public static final String USER_UPDATED = "User updated successfully.";

    // Error Messages
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String EMAIL_ALREADY_EXISTS = "Email is already registered.";
    public static final String PHONE_ALREADY_EXISTS = "Phone number is already registered.";

    // Intruder
    public static final String UNAUTHORIZED_USER = "Phone number is already registered.";


    // JWT ERROR MESSAGES
    public static final String INVALID_TOKEN = "INVALID TOKEN";
    public static final String EXPIRED_TOKEN = "INVALID TOKEN";

   // SERVER FAILURES
   public static final String FAIL_TO_EXECUTE = "Something went wrong";

}
