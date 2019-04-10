package com.bookinggo.ondemand.taxi.grab;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class TEMPLATE_SearchAndBookingHelpers {

    public static final String testAccountId = "100001";

    public static final String supplierName = "DiDi";


    static ExtractableResponse<Response> searchForRides(String payload){

        System.out.println("SEARCHING FOR RIDES....");

        ExtractableResponse<Response> response =
        given().
                log().
                ifValidationFails().
                body(payload).
                when().
                post("rides").
                then().
                log().
                all().
                extract();
        return response;
    }

    static String getSearchId(){

        ExtractableResponse<Response> ridesSearchResponse;
        JsonPath responseJson;

        ridesSearchResponse = searchForRides(TEMPLATE_SearchRequestBuilder.searchRequestPayload(testAccountId));
        responseJson = ridesSearchResponse.body().jsonPath();
        String searchResultId = responseJson.get("searchResults[0].products[0].searchResultId");
        return searchResultId;
    }


    static Response bookARide(String testAccountId) {


        // Book ride
        String bookingId;
        String postBodyJson = TEMPLATE_BookingRequestBuilder.bookingRequestPayload(getSearchId(), testAccountId);

        Response response =
                given().
                log().
                ifValidationFails().
                body(postBodyJson).
                when().
                post("bookings").

                then().
                log().
                all().
                body(matchesJsonSchemaInClasspath("TEMPLATE-schema.json")).

                extract().response();

                bookingId = response.path("bookingId");
                waitUntilSupplierAcceptsBooking(bookingId);

                return response;

    }

    static void waitUntilSupplierAcceptsBooking(String bookingId) {

        String currentStatus = null;
        for (int i = 0; i < 5; i++) {

            currentStatus = getBookingDetails(bookingId).body().jsonPath().get("status");

            if (currentStatus.equals("SUPPLIER_PENDING")) {
                return;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Current Status: " + currentStatus);
        throw new RuntimeException("timeout waiting for supplier to accept booking");
    }


    static ExtractableResponse<Response> getBookingDetails(String bookingId){

        return given().
                log().
                ifValidationFails().
                when().
                get("bookings/" + bookingId).
                then().
                extract();
    }

    static String getLatestSupplierBookingRefForCustomer(String customerAccountId){

        System.out.println("Finding the SupplierBookingRef");
        String path = "latest-customer-booking/";
        Response response =
                when().get(path + customerAccountId).
                then().extract().response();

        return response.path("supplierBookingRef");
    }

    static ExtractableResponse<Response> getLatestBookingForCustomer(String customerAccountId)  {

        String path = "latest-customer-booking/";
        System.out.println("TEST: Find Customers last booking and status");

        ExtractableResponse<Response> response =
                given().
                log().
                ifValidationFails().
                        when().
                        get(path + customerAccountId).

                        then().
                        log().
                        all().
                        statusCode(200).
                        extract();

        String didiStatus = response.path("status");
        System.out.println("DiDi Status: " + didiStatus);

        return response;
    }



    static ExtractableResponse<Response> customerCancelBooking (String customerAccountId)  {


        String path = "latest-customer-booking/";

        System.out.println("CUSTOMER CANCELS BOOKING....");

        ExtractableResponse<Response> response =
                given().
                        log().
                        ifValidationFails().
                        body(TEMPLATE_CustomerCancelBookingBuilder.customerCancelBookingPayload()).
                        when().
                        post(path + customerAccountId).

                        then().
                        log().
                        all().
                        extract();

        return response;
    }


    static String generateWebHookPayload(String bookingId, String supplierBookingRef, String status){
        return WebHookPayloadBuilder.build(status, bookingId, supplierBookingRef);
    }

    private static String createHMacSignature(String payload) {

        String webHookSignKey = "booking20180927";
        String hmacSHA1 = "HmacSHA1";

        try{
            Mac sha1_HMAC = Mac.getInstance(hmacSHA1);
            SecretKeySpec secret_key = new SecretKeySpec(webHookSignKey.getBytes(StandardCharsets.UTF_8), hmacSHA1);
            sha1_HMAC.init(secret_key);
            return Base64.encodeBase64String(sha1_HMAC.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception  e){
            throw new RuntimeException(e.getMessage(),e);
        }



    }

    static void updateStatus(String payload) {

        given().
                header("Authorization", TEMPLATE_SearchAndBookingHelpers.createHMacSignature(payload)).
                header("Content-Type", "application/json").
                body(payload).

                when().
                post("webhooks/bookings").

                then().
                log().
                all().
                statusCode(200);
    }
}
