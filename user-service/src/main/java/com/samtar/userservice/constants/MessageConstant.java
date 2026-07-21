package com.samtar.userservice.constants;

public final class MessageConstant {
    private MessageConstant() {}

    // Validation Messages
    public static final String INVALID_CREDENTIALS = "Invalid credentials.";
    public static final String REQUIRED = "This field is required.";
    public static final String INVALID_EMAIL = "Please enter a valid email address.";
    public static final String INVALID_PHONE = "Please enter a valid phone number.";
    public static final String INVALID_USERNAME = "Username format is invalid.";
    public static final String INVALID_PASSWORD = "Password does not meet security requirements.";
    public static final String ACCOUNT_DISABLED = "Account is disabled.";
    public static final String ACCOUNT_LOCKED = "Account is locked.";

    // Success Messages
    public static final String USER_CREATED = "User created successfully.";
    public static final String USER_SIGNIN = "User Sign in successfully.";
    public static final String USER_UPDATED = "User updated successfully.";

    // Error Messages
    public static final String USER_NOT_FOUND = "User not found.";
    public static final String EMAIL_ALREADY_EXISTS = "Email is already registered.";
    public static final String PHONE_ALREADY_EXISTS = "Phone number is already registered.";
    public static final String USERNAME_ALREADY_EXISTS = "Username is already registered.";
    public static final String SESSION_LIMIT_REACHED = "Maximum number of active sessions reached.";

    // Intruder
    public static final String UNAUTHORIZED_USER = "Phone number is already registered.";


    // JWT ERROR MESSAGES
    public static final String INVALID_TOKEN = "INVALID TOKEN";
    public static final String EXPIRED_TOKEN = "INVALID TOKEN";

   // SERVER FAILURES
   public static final String FAIL_TO_EXECUTE = "Something went wrong";


   // Sucess



   // generic
    public static final String INVALID_JSON = "Invalid JSON format";

  // generic
    public static final String INVALID_PAYLOAD = "Invalid payload.";
    public static final String METHOD_NOT_ALLOWED = "Method not allowed.";
}
