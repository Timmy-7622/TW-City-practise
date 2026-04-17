package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// urlPatterns = { "/getCities", "/getDistricts" }：這告訴伺服器，只要網址最後是這兩個名字，都歸這個 Servlet 管。
@WebServlet(urlPatterns = { "/getCities", "/getDistricts" })
public class AreaServlet extends HttpServlet {

    private static final String URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String USER = "FIN";
    private static final String PASSWORD = "!QAZ2wsx";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String servletPath = req.getServletPath();

        resp.setContentType("application/json;charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        // Access-Control-Allow-Origin:
        // *：這是開放**「跨網域訪問」**。因為你的網頁跟後端可能跑在不同的地方，沒加這一行，瀏覽器會為了安全把後端回傳的資料擋掉（這就是著名的 CORS 問題）
        resp.setHeader("Access-Control-Allow-Origin", "*");

        if ("/getCities".equals(servletPath)) {
            getCities(resp);
            // 多傳了一個req是因為，我們要把選擇的縣市傳出去才知道對應的區域有哪些
        } else if ("/getDistricts".equals(servletPath)) {
            getDistricts(req, resp);
        }
    }

    private void getCities(HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        StringBuilder json = new StringBuilder();
        json.append("[");

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            String sql = "SELECT CITY_NAME FROM TW_CITY ORDER BY CITY_ID";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    json.append(",");
                }
                json.append("\"").append(rs.getString("CITY_NAME")).append("\"");
                first = false;
            }

            json.append("]");
            // 關閉結果集
            // 關閉指令
            // 關閉連線
            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            json = new StringBuilder("[]");
        }

        out.print(json.toString());
        out.flush();
        out.close();
    }

    private void getDistricts(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String city = req.getParameter("city");

        PrintWriter out = resp.getWriter();
        StringBuilder json = new StringBuilder();
        json.append("[");

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            String sql = """
                    SELECT D.DISTRICT_NAME
                    FROM TW_DISTRICT D
                    JOIN TW_CITY C ON D.CITY_ID = C.CITY_ID
                    WHERE C.CITY_NAME = ?
                    ORDER BY D.DISTRICT_NAME
                    """;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, city);
            ResultSet rs = pstmt.executeQuery();

            boolean first = true;
            while (rs.next()) {
                if (!first) {
                    json.append(",");
                }
                json.append("\"").append(rs.getString("DISTRICT_NAME")).append("\"");
                first = false;
            }

            json.append("]");

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            json = new StringBuilder("[]");
        }

        out.print(json.toString());
        out.flush();
        out.close();
    }
}