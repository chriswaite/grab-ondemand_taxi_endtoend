package config;

import io.restassured.RestAssured;
import io.restassured.builder.*;
import io.restassured.specification.RequestSpecification;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

import java.io.IOException;
import java.sql.SQLException;

public class TestConfig {

    @Rule
    public TestRule watcher = new TestWatcher() {
        protected void starting(Description description) {
            System.out.println("PROCESSING JOB...... " + description.getMethodName());
        }
    };

    @BeforeClass

    public static void setup() throws IOException, SQLException {

        DbHelper dataSetup = new DbHelper();
        dataSetup.clearDataForUserInDatabase();


        RestAssured.baseURI = "https://ondemand.dev.someonedrive.me";
        RestAssured.basePath = "/api/v1/";

        RequestSpecification requestSpecification = new RequestSpecBuilder().
                addHeader("Content-Type", "application/json").
                addHeader("Accept", "application/json").
                build();

        RestAssured.requestSpecification = requestSpecification;


    }

    @Rule
    public TestName name = new TestName();

}

