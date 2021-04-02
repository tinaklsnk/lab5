import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import javax.xml.parsers.*;
import  org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Program {
    public static void main(String[] args) {
        Connection connection;

        String [] commands = {"1 - Вивести вміст таблиці у консоль",
                "2 - Видалити з таблиці запис із певним id",
                "3 - Вивести записи, координати точок яких лежать у заданих межах",
                "4 - Модифікувати за введеним id поле з описом точки",
                "5 - Очистити вміст таблиці",
                "6 - вихід"};
        for (String a: commands) {
            System.out.println(a);
        }
        Scanner scanner = new Scanner(System.in);
        while (scanner.nextInt() != 6) {
            switch (scanner.next()) {
                default:
                    System.out.println("Error");
            }
        }


        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/points?serverTimezone=UTC&useSSL=false", "root", "0000");
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
        try {
            assert connection != null;
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("SELECT * FROM points;");
            while (result.next())
                System.out.println(result.getInt(1) + result.getDouble(2)
                        + result.getDouble(3) + result.getString(4));
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    public static void show(Connection connection) {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM currency");
            while (rs.next())
                System.out.println(
                        rs.getInt(1) + " " +
                                rs.getString(2) + " " +
                                rs.getString(3));
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    public static void insert(Connection connection) throws ParserConfigurationException, IOException, SAXException, SQLException {
        File inputFile = new File("D:\\Studying\\Java\\lab4\\L-35-003-points.gpx");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(inputFile);
        doc.getDocumentElement().normalize();
        NodeList wpt = doc.getDocumentElement().getElementsByTagName("wpt");
        NodeList desc = doc.getDocumentElement().getElementsByTagName("desc");
        //NodeList link = doc.getDocumentElement().getElementsByTagName("link");
        String text, attribute;
        double lon = 0, lat = 0;
        int l = desc.getLength();
        connection.createStatement().executeUpdate("Truncate TABLE points");
        for (int i = 0; i < wpt.getLength(); i++) {
            text = desc.item(i).getTextContent();
            //attribute = link.item(i).getAttributes().item(0).getTextContent();
            lat = Double.parseDouble(wpt.item(i).getAttributes().item(0).getTextContent());
            lon = Double.parseDouble(wpt.item(i).getAttributes().item(1).getTextContent());
            String insertion = "INSERT INTO points (lat, lon, desc) values(?,?,?)";
            PreparedStatement statement = connection.prepareStatement(insertion);
            statement.setDouble(1, lat);
            statement.setDouble(2, lon);
            statement.setString(3, text);
            statement.execute();
        }
    }
}
