package com.samtar.inventoryservice.constants;

public final class MessageConstant {

  private MessageConstant() {
  }

  // =========================
  // Validation Messages
  // =========================
  public static final String INVENTORY_ID_MANDATORY = "Inventory Id is required.";
  public static final String PRODUCT_ID_MANDATORY = "Product Id is required.";
  public static final String WAREHOUSE_ID_MANDATORY = "Warehouse Id is required.";
  public static final String STOCK_QUANTITY_MANDATORY = "Stock quantity is required.";
  public static final String STOCK_QUANTITY_INVALID = "Stock quantity cannot be negative.";
  public static final String STOCK_ADJUSTMENT_MANDATORY = "Stock adjustment quantity is required.";
  public static final String STOCK_ADJUSTMENT_INVALID = "Stock adjustment quantity cannot be zero.";
  public static final String REORDER_LEVEL_INVALID = "Reorder level cannot be negative.";
  public static final String MINIMUM_STOCK_LEVEL_INVALID = "Minimum stock level cannot be negative.";
  public static final String MAXIMUM_STOCK_LEVEL_INVALID = "Maximum stock level cannot be negative.";
  public static final String RESERVED_QUANTITY_INVALID = "Reserved quantity cannot be negative.";
  public static final String AVAILABLE_QUANTITY_INVALID = "Available quantity cannot be negative.";

  public static final String INVENTORY_STATUS_MANDATORY = "Inventory status is required.";
  public static final String INVENTORY_LOCATION_MANDATORY = "Inventory location is required.";
  public static final String INVENTORY_REASON_MANDATORY = "Inventory adjustment reason is required.";

  // =========================
  // Inventory Exceptions
  // =========================
  public static final String INVENTORY_NOT_FOUND = "Inventory not found.";
  public static final String INVENTORY_ALREADY_EXISTS = "Inventory already exists.";
  public static final String INVENTORY_SAVE_FAILED = "Unable to save inventory.";
  public static final String INVENTORY_UPDATE_FAILED = "Unable to update inventory.";
  public static final String INVENTORY_DELETE_FAILED = "Unable to delete inventory.";
  public static final String INVENTORY_IN_USE = "Inventory cannot be deleted because it is in use.";

  public static final String PRODUCT_NOT_FOUND = "Product not found.";
  public static final String WAREHOUSE_NOT_FOUND = "Warehouse not found.";
  public static final String UNAUTHORIZED_USER = "Unauthorized user.";

  // =========================
  // Stock Messages
  // =========================
  public static final String OUT_OF_STOCK = "Item is out of stock.";
  public static final String INSUFFICIENT_STOCK = "Insufficient stock available.";
  public static final String STOCK_NOT_AVAILABLE = "Requested stock is not available.";
  public static final String STOCK_ALREADY_RESERVED = "Stock is already reserved.";
  public static final String INSUFFICIENT_AVAILABLE_STOCK = "Insufficient available stock.";
  public static final String STOCK_RESERVATION_FAILED = "Unable to reserve stock.";
  public static final String STOCK_RELEASE_FAILED = "Unable to release reserved stock.";
  public static final String STOCK_ADJUSTMENT_FAILED = "Unable to adjust stock.";

  // =========================
  // Success Messages
  // =========================
  public static final String INVENTORY_CREATED_SUCCESS = "Inventory created successfully.";
  public static final String INVENTORY_UPDATED_SUCCESS = "Inventory updated successfully.";
  public static final String INVENTORY_DELETED_SUCCESS = "Inventory deleted successfully.";
  public static final String INVENTORY_FETCHED_SUCCESS = "Inventory retrieved successfully.";

  public static final String STOCK_UPDATED_SUCCESS = "Stock updated successfully.";
  public static final String STOCK_ADJUSTED_SUCCESS = "Stock adjusted successfully.";
  public static final String STOCK_RESERVED_SUCCESS = "Stock reserved successfully.";
  public static final String STOCK_RELEASED_SUCCESS = "Reserved stock released successfully.";

  public static final String INVENTORY_STATUS_UPDATED_SUCCESS =
          "Inventory status updated successfully.";

  // =========================
  // Warning Messages
  // =========================
  public static final String LOW_STOCK_WARNING = "Stock level is running low.";
  public static final String REORDER_LEVEL_REACHED = "Stock has reached the reorder level.";
  public static final String OUT_OF_STOCK_WARNING = "Stock level has reached zero.";
  public static final String INVENTORY_INACTIVE_WARNING = "Inventory is inactive.";
  public static final String INVENTORY_ALREADY_ACTIVE = "Inventory is already active.";
  public static final String INVENTORY_ALREADY_INACTIVE = "Inventory is already inactive.";

  // =========================
  // Stock Validation
  // =========================
  public static final String STOCK_CANNOT_EXCEED_MAXIMUM =
          "Stock quantity cannot exceed the maximum stock level.";

  public static final String RESERVED_STOCK_CANNOT_EXCEED_AVAILABLE =
          "Reserved stock cannot exceed available stock.";

  public static final String STOCK_RELEASE_CANNOT_EXCEED_RESERVED =
          "Released quantity cannot exceed reserved stock.";

  public static final String STOCK_DEDUCTION_CANNOT_EXCEED_AVAILABLE =
          "Deducted quantity cannot exceed available stock.";

  // =========================
  // Inventory Adjustment
  // =========================
  public static final String INVALID_ADJUSTMENT_TYPE = "Invalid inventory adjustment type.";
  public static final String INVALID_STOCK_TRANSACTION = "Invalid stock transaction.";
  public static final String STOCK_TRANSACTION_NOT_FOUND = "Stock transaction not found.";
  public static final String STOCK_TRANSACTION_FAILED = "Unable to process stock transaction.";

  // =========================
  // Generic Messages
  // =========================
  public static final String INVALID_JSON = "Invalid JSON format.";
  public static final String INVALID_PAYLOAD = "Invalid payload.";
  public static final String METHOD_NOT_ALLOWED = "Method not allowed.";
  public static final String FAIL_TO_EXECUTE = "Something went wrong.";
}
