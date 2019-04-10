package com.bookinggo.ondemand.taxi.grab;

import config.DbHelper;
import config.TestConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.Test;
import java.io.IOException;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TEMPLATE_BookARide extends TestConfig {

    /***
     * Given I have a Search ID
     And I have a valid customer account ID
     When I Post to the endpoint
     Then I should get a 201 status code
     And the response body matches the schema
     And the customer's 'latest booking' has the correct status
     */

    private final String path = "bookings";
    private String customerAccountId = TEMPLATE_SearchAndBookingHelpers.testAccountId;
    private ExtractableResponse<Response> lastBookingResponse;
    private String status;
    private String thisStatus;

    @Test
    public void bookARideUsingSearchId() throws IOException, SQLException {

        DbHelper.clearDataForUserInDatabase();

        Response bookingResponse = TEMPLATE_SearchAndBookingHelpers.bookARide(customerAccountId);
        JsonPath bookingResponseJson = bookingResponse.body().jsonPath();

        assertEquals(201, bookingResponse.statusCode());
        assertNotNull(bookingResponseJson.get("bookingId"));

        /***
         Find customers latest booking and verify the status
         */

        status = "SUPPLIER_PENDING";
        lastBookingResponse = TEMPLATE_SearchAndBookingHelpers.getLatestBookingForCustomer(customerAccountId);
        thisStatus = lastBookingResponse.jsonPath().get("status");
        assertThat(thisStatus).isEqualTo(status);

    }

    @Test
    public void unsuccessfulBookingRequest() throws IOException, SQLException {

        DbHelper.clearDataForUserInDatabase();

        System.out.println("TEST: Attempt to create a booking with invalid ride ID");

        String invalidSearchResultId = (TEMPLATE_SearchAndBookingHelpers.getSearchId() + "A");

        String postBodyJson = TEMPLATE_BookingRequestBuilder.bookingRequestPayload(invalidSearchResultId, TEMPLATE_SearchAndBookingHelpers.testAccountId);

        given().
                log().
                ifValidationFails().
                body(postBodyJson).

                when().
                post(path).

                then().
                log().
                all().
                statusCode(500).
                body("code", is("supplier.search.result.not.retrieved")).
                body("message", not(isEmptyOrNullString()));

    }
}
