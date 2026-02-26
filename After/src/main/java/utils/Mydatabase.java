package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Mydatabase {
    private static Mydatabase instance ;
final String URL ="jdbc:mysql://127.0.0.1:3306/after";
    final String USERNAME = "root";
    final String PASSWORD = "";
    private Connection cnx;
    private Mydatabase() {
        try {
            this.cnx= DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("Connected .......");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static Mydatabase getInstance() {
        if (instance == null) {
            instance = new Mydatabase();
        }
        return instance;
    }
    public Connection getCnx() {
        return cnx;
    }
}
