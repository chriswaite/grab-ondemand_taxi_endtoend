package config;

import com.bookinggo.ondemand.taxi.grab.TEMPLATE_SearchAndBookingHelpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DbHelper {

    private static PreparedStatement executeStatement;

    public static void clearDataForUserInDatabase() throws IOException, SQLException {

        BufferedReader reader = null;
        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();

            String customerAccountId = TEMPLATE_SearchAndBookingHelpers.testAccountId;
            String updateString = "DELETE from booking where customer_account_id = '"+ customerAccountId + "'";

            // create statement object
            executeStatement = con.prepareStatement(updateString);

            //Execute statement
            executeStatement.executeUpdate();

            System.out.println("SQL query run successfully: Test data created");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // close file reader
            if (reader != null) {
                reader.close();
            }
            // close db connection
            if (con != null) {
                con.close();
            }
        }
    }
}