package utils;

public interface TestConstants {
    String GROUP_NAME = "Computers";
    String GROUP_JSON = "{ \"name\": \"" + GROUP_NAME + "\" }";
    String GROUP_JSON_EMPTY_NAME = "{ \"name\": \"\" }";

    String PRODUCT_NAME = "phone";
    String PRODUCT_VENDOR = "Samsung";
    String PRODUCT_COUNT = "10";
    String PRODUCT_PRICE = "153.7";
    String PRODUCT_GROUP = "Computers";

    String PRODUCT_BAD_COUNT = "-2";
    String PRODUCT_BAD_PRICE = "-2.0";

    String COUNT_PRODUCT_NAME = "phone";
    String COUNT_PRODUCT_COUNT = "5";
    String COUNT_PRODUCT_TOOMUCH_COUNT = PRODUCT_COUNT + "0";
    String COUNT_PRODUCT_BAD_COUNT = "-3";

    String PRODUCT_JSON = "{ \"name\": \"" + PRODUCT_NAME + "\", \"vendor\": \"" + PRODUCT_VENDOR + "\", \"count\": " + PRODUCT_COUNT + ", \"price\": " + PRODUCT_PRICE + ", \"group\": \"" + PRODUCT_GROUP + "\" }";

    String PRODUCT_JSON_EMPTY_NAME = "{ \"name\": \"\", \"vendor\": \"" + PRODUCT_VENDOR + "\", \"count\": " + PRODUCT_COUNT + ", \"price\": " + PRODUCT_PRICE + ", \"group\": \"" + PRODUCT_GROUP + "\" }";
    String PRODUCT_JSON_EMPTY_VENDOR = "{ \"name\": \"" + PRODUCT_NAME + "\", \"vendor\": \"\", \"count\": " + PRODUCT_COUNT + ", \"price\": " + PRODUCT_PRICE + ", \"group\": \"" + PRODUCT_GROUP + "\" }";
    String PRODUCT_JSON_BAD_COUNT = "{ \"name\": \"" + PRODUCT_NAME + "\", \"vendor\": \"" + PRODUCT_VENDOR + "\", \"count\": " + PRODUCT_BAD_COUNT + ", \"price\": " + PRODUCT_PRICE + ", \"group\": \"" + PRODUCT_GROUP + "\" }";
    String PRODUCT_JSON_BAD_PRICE = "{ \"name\": \"" + PRODUCT_NAME + "\", \"vendor\": \"" + PRODUCT_VENDOR + "\", \"count\": " + PRODUCT_COUNT + ", \"price\": " + PRODUCT_BAD_PRICE + ", \"group\": \"" + PRODUCT_GROUP + "\" }";
    String PRODUCT_JSON_EMPTY_GROUP = "{ \"name\": \"" + PRODUCT_NAME + "\", \"vendor\": \"" + PRODUCT_VENDOR + "\", \"count\": " + PRODUCT_COUNT + ", \"price\": " + PRODUCT_PRICE + ", \"group\": \"\" }";

    String COUNT_PRODUCT_JSON = "{ \"name\": \"" + COUNT_PRODUCT_NAME + "\", \"count\": " + COUNT_PRODUCT_COUNT + " }";

    String COUNT_PRODUCT_JSON_EMPTY_NAME = "{ \"name\": \"\", \"count\": " + COUNT_PRODUCT_COUNT + " }";
    String COUNT_PRODUCT_JSON_BAD_COUNT = "{ \"name\": \"" + COUNT_PRODUCT_NAME + "\", \"count\": " + COUNT_PRODUCT_BAD_COUNT + " }";
    String COUNT_PRODUCT_JSON_TOOMUCH_COUNT = "{ \"name\": \"" + COUNT_PRODUCT_NAME + "\", \"count\": " + COUNT_PRODUCT_TOOMUCH_COUNT + " }";

    String SEARCH_JSON = "{\"nameStart\":\"phone\",\"descriptionStart\":\"Some phone\",\"vendorStart\":\"Samsung\",\"countFrom\":5,\"countTo\":10,\"priceFrom\":15,\"priceTo\":20,\"groupStart\":\"test\"}";

    String NAME_EMPTY_MESSAGE = "Name must be not empty";
    String VENDOR_EMPTY_MESSAGE = "Vendor must be not empty";
    String COUNT_BAD_MESSAGE = "Count must be greater than 0";
    String PRICE_BAD_MESSAGE = "Price must be greater than 0";
    String GROUP_EMPTY_MESSAGE = "Group must be not empty";
}
