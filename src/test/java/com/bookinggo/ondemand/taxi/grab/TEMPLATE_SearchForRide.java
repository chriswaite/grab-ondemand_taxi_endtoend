package com.bookinggo.ondemand.taxi.grab;

import config.TestConfig;
import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;
import static com.bookinggo.ondemand.taxi.grab.TEMPLATE_SearchAndBookingHelpers.searchForRides;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.*;


public class TEMPLATE_SearchForRide extends TestConfig {

    ExtractableResponse<Response> ridesSearchResponse;
    JsonPath responseJson;

    @Test
    public void searchForAvailableRides() {

        ridesSearchResponse = searchForRides(TEMPLATE_SearchRequestBuilder.searchRequestPayload(TEMPLATE_SearchAndBookingHelpers.testAccountId));
        responseJson = ridesSearchResponse.body().jsonPath();
        assertEquals(200, ridesSearchResponse.statusCode());
        Assert.assertThat(responseJson.get(), hasKey("searchResults"));
        assertNotNull(responseJson.get("searchResults[0].products[0].searchResultId"));
        assertNotNull(responseJson.get("searchResults[0].products[0].price.estimate.amount"));
        assertNotNull(responseJson.get("searchResults[0].products[0].price.estimate.currencyCode"));
        assertEquals(TEMPLATE_SearchAndBookingHelpers.supplierName, responseJson.get("searchResults[0].products[0].supplier.name"));
        assertNotNull(responseJson.get("searchResults[0].products[0].supplier.logoUrl"));
    }

    @Test
    public void searchForRidesInvalidLocation() {

        ridesSearchResponse = searchForRides(TEMPLATE_SearchRequestBuilder.searchRequestPayloadInvalid(TEMPLATE_SearchAndBookingHelpers.testAccountId));
        responseJson = ridesSearchResponse.body().jsonPath();
        assertEquals(500, ridesSearchResponse.statusCode());
        assertEquals("supplier.error", responseJson.get("code"));
        String responseErrorMessage = (responseJson.get("message"));
        assertTrue(responseErrorMessage.contains("Supplier Error"));
    }
}




