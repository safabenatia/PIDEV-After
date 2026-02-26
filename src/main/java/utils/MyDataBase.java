package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDataBase {

    private static MyDataBase instance;
     final String URL="jdbc:mysql://localhost:3307/after";
     final String USERNAME="root";
     final String PASSWORD="";
     private Connection cnx;

    private MyDataBase() {
        try {
            cnx= DriverManager.getConnection(URL,USERNAME,PASSWORD);
            System.out.println("connected");

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public static MyDataBase getMyInstance() {
        if (instance == null)
            instance = new MyDataBase();
        return instance;
        }



    public Connection getCnx() {
        return cnx;
    }


}
