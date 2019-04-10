package config;

import java.sql.PreparedStatement;
import java.sql.*;


public class DatabaseConnection {

    private static String driverName = "com.mysql.cj.jdbc.Driver";
    private static String url = "jdbc:mysql://ondemand-taxi-booking-service-db.us-west-2-dev.internal.dev.someonedrive.me/ondemandtaxibookingservice";
    private static String userName = "ondemandtaxibookingservice";
    private static String passWord = "txXryNWS4lctC5eD4aFSAG3bdsfY6uNG";
    public static Connection con;


    public static Connection getConnection() {
        try {

            Class driver = Class.forName(driverName);
            System.out.println(driver.getName());
            try {
                con = DriverManager.getConnection(url, userName, passWord);
                System.out.println("TEST");
            } catch (SQLException ex) {
                ex.printStackTrace();
                // log an exception:
                System.out.println("Failed to create the database connection.");
            }
        } catch (ClassNotFoundException ex) {
            // log an exception:
            System.out.println("Driver not found.");
            ex.printStackTrace();
        }
        System.out.println("Database Connection established...");
        return con;
    }
}
