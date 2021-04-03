import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Program {
    public static Connection connection;
    public static void main(String[] args) throws SQLException, ParserConfigurationException, SAXException, IOException {
        String [] commands = {"1 - Вивести вміст таблиці у консоль",
                "2 - Видалити з таблиці запис із певним id",
                "3 - Вивести записи, координати точок яких лежать у заданих межах",
                "4 - Модифікувати за введеним id поле з описом точки",
                "5 - Очистити вміст таблиці",
                "6 - вихід"};
        for (String a: commands) {
            System.out.println(a);
        }
        connect();
        try {
            assert connection != null;
            insert();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        for (;;) {
            switch (scanner.nextInt()) {
                case 1:
                    show(connection);
                    break;
                case 3:
                    showLimited(connection);
                    break;
                case 6:
                    System. exit(0);
                default:
                    System.out.println("Error");
            }
        }
    }

    public static void show(Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM points");
            while (rs.next())
                System.out.println(
                                rs.getInt(1) + " " +
                                rs.getDouble(2) + " " +
                                rs.getDouble(3) + " " +
                                rs.getString(4));
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public void deleteRow(Connection connection, int row) throws SQLException {
        Statement stmt = connection.createStatement();
        int rs = stmt.executeUpdate("DELETE  FROM points WHERE id=" + row);
    }

    public static void showLimited(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        System.out.println("Limits: ");
        ResultSet rs = stmt.executeQuery("SELECT * FROM points WHERE (lat BETWEEN 47 and 48) and (lon BETWEEN  25 and 26)");
        while (rs.next()) {
            System.out.println(
                            rs.getInt(1) + " " +
                            rs.getDouble(2) + " " +
                            rs.getDouble(3) + " " +
                            rs.getString(4));
        }
    }

    public static void modify(Connection connection, String text, int id) throws SQLException {
        PreparedStatement prstmt = connection.prepareStatement("UPDATE points SET description=? WHERE id=?" );
        prstmt.setString(1, text);
        prstmt.setInt(2, id);
        prstmt.executeUpdate();
    }

    public static void clear(Connection connection) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("TRUNCATE TABLE points");
    }

    public static void insert() throws ParserConfigurationException, IOException, SAXException, SQLException {
        File inputFile = new File("D:\\Studying\\Java\\lab4\\L-35-003-points.gpx");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        NodeList wpt = doc.getDocumentElement().getElementsByTagName("wpt");
        NodeList desc = doc.getDocumentElement().getElementsByTagName("desc");
        String text;
        double lon = 0, lat = 0;
        connection.createStatement().executeUpdate("Truncate TABLE points");
        for (int i = 0; i < desc.getLength(); i++)
        {
            text = desc.item(i).getTextContent();
            lat = Double.parseDouble(wpt.item(i).getAttributes().item(0).getTextContent());
            lon = Double.parseDouble(wpt.item(i).getAttributes().item(1).getTextContent());
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO points (lat, lon, description) values(?,?,?)");
            stmt.setDouble(1, lat);
            stmt.setDouble(2, lon);
            stmt.setString(3, text);
            stmt.execute();
            System.out.println("Inserted!!!");
        }
    }

    public static void connect() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/lab5?serverTimezone=UTC&useSSL=false", "root", "0000");
        } catch (SQLException throwables) {
            System.out.println("Connection failed...");
            throwables.printStackTrace();
            return;
        }
        if (connection != null) {
            System.out.println("Connection established");
        } else {
            System.out.println("connection == null");
        }
    }
}
