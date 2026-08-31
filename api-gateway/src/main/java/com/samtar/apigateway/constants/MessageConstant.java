package com.samtar.apigateway.constants;


public final class MessageConstant {

  private MessageConstant() {
  }
  public static final String FAIL_TO_EXECUTE = "Something went wrongsss";

  // API Gateway Errors
  public static final String UNAUTHORIZED =
          "Unauthorized request";

  public static final String FORBIDDEN =
          "Access denied";

  public static final String NOT_FOUND =
          "Resource not found";

  public static final String BAD_REQUEST =
          "Invalid request";

  public static final String REQUEST_TIMEOUT =
          "Request timed out";

  public static final String TOO_MANY_REQUESTS =
          "Too many requests";

  public static final String BAD_GATEWAY =
          "Invalid response from downstream service";

  public static final String SERVICE_UNAVAILABLE =
          "Downstream service is unavailable";

  public static final String GATEWAY_TIMEOUT =
          "Downstream service request timed out";

  public static final String UNAUTHORIZED_USER = "Unauthorized user.";

  public static final String INTERNAL_SERVER_ERROR =
          "An unexpected error occurred at the API Gateway";

  public static final String INVALID_TOKEN =
          "Invalid token";

  public static final String SESSION_EXPIRED =
          "Session Expired";

  public static final String TOKEN_EXPIRED =
          "Token Expired";
}

