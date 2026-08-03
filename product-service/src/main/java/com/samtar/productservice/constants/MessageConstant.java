package com.samtar.productservice.constants;

public final class MessageConstant {
  private MessageConstant() {
  }



  // =========================
  // Validation Messages
  // =========================
  public static final String PRODUCT_NAME_MANDATORY = "Product name cannot be blank.";
  public static final String PRODUCT_CODE_MANDATORY = "Product code cannot be blank.";
  public static final String PRODUCT_CATEGORY_MANDATORY = "Product category is required.";
  public static final String PRODUCT_PRICE_MANDATORY = "Product price is required.";
  public static final String PRODUCT_PRICE_INVALID = "Product price must be greater than zero.";
  public static final String PRODUCT_COST_INVALID = "Product cost cannot be negative.";
  public static final String PRODUCT_STOCK_INVALID = "Stock quantity cannot be negative.";
  public static final String PRODUCT_UNIT_MANDATORY = "Unit is required.";
  public static final String PRODUCT_BRAND_MANDATORY = "Brand is required.";
  public static final String PRODUCT_DESCRIPTION_MANDATORY = "Product description cannot be blank.";

  // =========================
  // Exception Messages
  // =========================
  public static final String PRODUCT_NOT_FOUND = "Product not found.";
  public static final String PRODUCT_ALREADY_EXISTS = "Product already exists.";
  public static final String PRODUCT_CODE_ALREADY_EXISTS = "Product code already exists.";
  public static final String PRODUCT_SKU_ALREADY_EXISTS = "SKU already exists.";
  public static final String PRODUCT_DELETE_FAILED = "Unable to delete product.";
  public static final String PRODUCT_UPDATE_FAILED = "Unable to update product.";
  public static final String PRODUCT_SAVE_FAILED = "Unable to save product.";
  public static final String PRODUCT_IN_USE = "Product cannot be deleted because it is in use.";
  public static final String PRODUCT_OUT_OF_STOCK = "Product is out of stock.";
  public static final String INSUFFICIENT_STOCK = "Insufficient stock available.";

  // =========================
  // Success Messages
  // =========================
  public static final String PRODUCT_CREATED_SUCCESS = "Product created successfully.";
  public static final String PRODUCT_UPDATED_SUCCESS = "Product updated successfully.";
  public static final String PRODUCT_DELETED_SUCCESS = "Product deleted successfully.";
  public static final String PRODUCT_FETCHED_SUCCESS = "Product retrieved successfully.";
  public static final String PRODUCT_STATUS_UPDATED = "Product status updated successfully.";
  public static final String PRODUCT_STOCK_UPDATED = "Product stock updated successfully.";

  // =========================
  // Warning Messages
  // =========================
  public static final String LOW_STOCK_WARNING = "Product stock is running low.";
  public static final String PRODUCT_INACTIVE_WARNING = "Product is inactive.";
  public static final String PRODUCT_DISCONTINUED_WARNING = "Product has been discontinued.";
  public static final String PRODUCT_ALREADY_ACTIVE = "Product is already active.";
  public static final String PRODUCT_ALREADY_INACTIVE = "Product is already inactive.";
  public static final String REORDER_LEVEL_REACHED = "Product has reached the reorder level.";


  public static final String PRODUCT_SKU_MANDATORY = "SKU cannot be blank.";
  public static final String CATEGORY_ID_MANDATORY = "Category is required.";
  public static final String BRAND_ID_MANDATORY = "Brand is required.";
  public static final String PRODUCT_SELLING_PRICE_INVALID = "Selling price must be greater than zero.";
  public static final String PRODUCT_COST_PRICE_INVALID = "Cost price cannot be negative.";
  public static final String PRODUCT_REORDER_LEVEL_INVALID = "Reorder level cannot be negative.";
  public static final String PRODUCT_SUPPLIER_INVALID_ID = "Invalid seller id";


  public static final String UNAUTHORIZED_USER = "Unauthorized user.";


}
