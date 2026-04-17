package servlet;

import java.sql.Connection;
import java.sql.DriverManager;

public class TestOracle {
    public static void main(String[] args) {
        String url = "jdbc:oracle:thin:@localhost:1521:XE";
        String user = "system";
        String password = "123456";

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Oracle 連線成功");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}