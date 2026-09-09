package com.samtar.warehouseservice.constants;

public final class MessageConstant {

  private MessageConstant() {
  }

  // =========================
  // Validation Messages
  // =========================
  public static final String WAREHOUSE_ID_MANDATORY = "Warehouse Id is required.";
  public static final String WAREHOUSE_NAME_MANDATORY = "Warehouse name is required.";
  public static final String WAREHOUSE_NAME_MAX_LENGTH = "Warehouse name cannot exceed 150 characters.";
  public static final String WAREHOUSE_CODE_MANDATORY = "Warehouse code is required.";
  public static final String WAREHOUSE_CODE_MAX_LENGTH = "Warehouse code cannot exceed 50 characters.";

  public static final String ADDRESS_LINE1_MANDATORY = "Address line 1 is required.";
  public static final String ADDRESS_LINE1_MAX_LENGTH = "Address line 1 cannot exceed 255 characters.";
  public static final String ADDRESS_LINE2_MAX_LENGTH = "Address line 2 cannot exceed 255 characters.";
  public static final String CITY_MANDATORY = "City is required.";
  public static final String CITY_MAX_LENGTH = "City cannot exceed 100 characters.";
  public static final String STATE_MANDATORY = "State is required.";
  public static final String STATE_MAX_LENGTH = "State cannot exceed 100 characters.";
  public static final String COUNTRY_MANDATORY = "Country is required.";
  public static final String COUNTRY_MAX_LENGTH = "Country cannot exceed 100 characters.";
  public static final String POSTAL_CODE_MANDATORY = "Postal code is required.";
  public static final String POSTAL_CODE_MAX_LENGTH = "Postal code cannot exceed 20 characters.";

  public static final String LATITUDE_INVALID = "Latitude must be between -90 and 90.";
  public static final String LONGITUDE_INVALID = "Longitude must be between -180 and 180.";

  public static final String WAREHOUSE_STATUS_MANDATORY = "Warehouse status is required.";
  public static final String SELLER_ID_INVALID = "Invalid seller id.";

  // =========================
  // Warehouse Exceptions
  // =========================
  public static final String WAREHOUSE_NOT_FOUND = "Warehouse not found.";
  public static final String WAREHOUSE_CODE_ALREADY_EXISTS = "Warehouse code already exists for this seller.";
  public static final String WAREHOUSE_SAVE_FAILED = "Unable to save warehouse.";
  public static final String WAREHOUSE_UPDATE_FAILED = "Unable to update warehouse.";
  public static final String WAREHOUSE_DELETE_FAILED = "Unable to delete warehouse.";
  public static final String WAREHOUSE_IN_USE = "Warehouse cannot be deleted because it is in use.";
  public static final String UNAUTHORIZED_USER = "Unauthorized user.";

  // =========================
  // Success Messages
  // =========================
  public static final String WAREHOUSE_CREATED_SUCCESS = "Warehouse created successfully.";
  public static final String WAREHOUSE_UPDATED_SUCCESS = "Warehouse updated successfully.";
  public static final String WAREHOUSE_DELETED_SUCCESS = "Warehouse deleted successfully.";
  public static final String WAREHOUSE_FETCHED_SUCCESS = "Warehouse retrieved successfully.";

  // =========================
  // Generic Messages
  // =========================
  public static final String INVALID_JSON = "Invalid JSON format.";
  public static final String INVALID_PAYLOAD = "Invalid payload.";
  public static final String METHOD_NOT_ALLOWED = "Method not allowed.";
  public static final String FAIL_TO_EXECUTE = "Something went wrong.";
}
