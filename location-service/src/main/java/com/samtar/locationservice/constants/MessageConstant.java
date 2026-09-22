package com.samtar.locationservice.constants;

public final class MessageConstant {

  private MessageConstant() {
  }

  // =========================
  // Validation Messages
  // =========================
  public static final String COUNTRY_ID_MANDATORY = "Country Id is required.";
  public static final String COUNTRY_NAME_MANDATORY = "Country name is required.";
  public static final String COUNTRY_NAME_MAX_LENGTH = "Country name cannot exceed 100 characters.";
  public static final String COUNTRY_ISO2_MANDATORY = "Country ISO2 code is required.";
  public static final String COUNTRY_ISO2_LENGTH = "Country ISO2 code must be exactly 2 characters.";
  public static final String COUNTRY_ISO3_MANDATORY = "Country ISO3 code is required.";
  public static final String COUNTRY_ISO3_LENGTH = "Country ISO3 code must be exactly 3 characters.";
  public static final String COUNTRY_PHONE_CODE_MAX_LENGTH = "Country phone code cannot exceed 10 characters.";

  public static final String STATE_ID_MANDATORY = "State Id is required.";
  public static final String STATE_NAME_MANDATORY = "State name is required.";
  public static final String STATE_NAME_MAX_LENGTH = "State name cannot exceed 100 characters.";
  public static final String STATE_CODE_MANDATORY = "State code is required.";
  public static final String STATE_CODE_MAX_LENGTH = "State code cannot exceed 10 characters.";

  public static final String CITY_ID_MANDATORY = "City Id is required.";
  public static final String CITY_NAME_MANDATORY = "City name is required.";
  public static final String CITY_NAME_MAX_LENGTH = "City name cannot exceed 100 characters.";

  // =========================
  // Not Found
  // =========================
  public static final String COUNTRY_NOT_FOUND = "Country not found.";
  public static final String STATE_NOT_FOUND = "State not found.";
  public static final String CITY_NOT_FOUND = "City not found.";

  // =========================
  // Conflicts
  // =========================
  public static final String COUNTRY_ALREADY_EXISTS = "Country already exists.";
  public static final String STATE_ALREADY_EXISTS = "State code already exists for this country.";
  public static final String CITY_ALREADY_EXISTS = "City name already exists for this state.";
  public static final String COUNTRY_HAS_STATES = "Country cannot be deleted because states are mapped to it.";
  public static final String STATE_HAS_CITIES = "State cannot be deleted because cities are mapped to it.";

  // =========================
  // Success Messages
  // =========================
  public static final String COUNTRY_CREATED_SUCCESS = "Country created successfully.";
  public static final String COUNTRY_UPDATED_SUCCESS = "Country updated successfully.";
  public static final String COUNTRY_DELETED_SUCCESS = "Country deleted successfully.";
  public static final String COUNTRY_FETCHED_SUCCESS = "Country retrieved successfully.";

  public static final String STATE_CREATED_SUCCESS = "State created successfully.";
  public static final String STATE_UPDATED_SUCCESS = "State updated successfully.";
  public static final String STATE_DELETED_SUCCESS = "State deleted successfully.";
  public static final String STATE_FETCHED_SUCCESS = "State retrieved successfully.";

  public static final String CITY_CREATED_SUCCESS = "City created successfully.";
  public static final String CITY_UPDATED_SUCCESS = "City updated successfully.";
  public static final String CITY_DELETED_SUCCESS = "City deleted successfully.";
  public static final String CITY_FETCHED_SUCCESS = "City retrieved successfully.";

  // =========================
  // Generic Messages
  // =========================
  public static final String UNAUTHORIZED_USER = "Unauthorized user.";
  public static final String INVALID_JSON = "Invalid JSON format.";
  public static final String INVALID_PAYLOAD = "Invalid payload.";
  public static final String METHOD_NOT_ALLOWED = "Method not allowed.";
  public static final String FAIL_TO_EXECUTE = "Something went wrong.";
}
