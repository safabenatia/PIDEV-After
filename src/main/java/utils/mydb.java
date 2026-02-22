package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class mydb {

    private static mydb instance;
    final String url="jdbc:mysql://127.0.0.1:3306/after";
    final String username="root";
    final String password="";
    private Connection cnx;

    private mydb(){
        try {
            this.cnx= DriverManager.getConnection(url,username,password);
            System.out.println("Connected ...");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public static mydb getInstance(){
        if (instance==null)
            instance=new mydb();
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }
}
