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
        commandList();
        connect();
        try {
            assert connection != null;
            insert();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        Scanner scanner = new Scanner(System.in);
        for (;;) {
            System.out.println("Enter the command number:");
            switch (scanner.nextInt()) {
                case 1:
                    show();
                    break;
                case 2:
                    deleteRow();
                    break;
                case 3:
                    showLimited();
                    break;
                case 4:
                    modify();
                    break;
                case 5:
                    clear();
                    break;
                case 6:
                    connection.close();
                    System. exit(0);
                    break;
                default:
                    System.out.println("Error");
                    break;
            }
        }
    }

    public static void show() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM points");
            System.out.println("Id\t" + "lat\t\t" + "\tlon\t" + "\t\t description");
            while (rs.next())
                System.out.println(
                                rs.getInt(1) + "\t" +
                                rs.getDouble(2) + "\t" +
                                rs.getDouble(3) + "\t" +
                                rs.getString(4));
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void deleteRow() throws SQLException {
        Statement stmt = connection.createStatement();
        System.out.println("Enter id:");
        Scanner in = new Scanner(System.in);
        int row = in.nextInt();
        stmt.executeUpdate("DELETE  FROM points WHERE id=" + row);
    }

    public static void showLimited() throws SQLException {
        Statement stmt = connection.createStatement();
        System.out.println("Limits:\tlat: 47.74-47.8\tlon: 25-25.1");
        ResultSet rs = stmt.executeQuery("SELECT * FROM points WHERE (lat BETWEEN 47.74 and 47.8) and (lon BETWEEN  25 and 25.1)");
        System.out.println("Id\t" + "lat\t\t" + "\tlon\t" + "\t\t description");
        while (rs.next()) {
            System.out.println(
                            rs.getInt(1) + "\t" +
                            rs.getDouble(2) + "\t" +
                            rs.getDouble(3) + "\t" +
                            rs.getString(4));
        }
    }

    public static void modify() throws SQLException {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter id:");
        int id = in.nextInt();
        System.out.println("Enter text:");
        String text = in.nextLine();
        PreparedStatement prstmt = connection.prepareStatement("UPDATE points SET description=? WHERE id=?" );
        prstmt.setString(1, text);
        prstmt.setInt(2, id);
        prstmt.executeUpdate();
    }

    public static void clear() throws SQLException {
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
        double lon, lat;
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

    public static void commandList() {
        String [] commands = {"1 - Вивести вміст таблиці у консоль",
                "2 - Видалити з таблиці запис із певним id",
                "3 - Вивести записи, координати точок яких лежать у заданих межах",
                "4 - Модифікувати за введеним id поле з описом точки",
                "5 - Очистити вміст таблиці",
                "6 - вихід"};
        for (String a: commands) {
            System.out.println(a);
        }
    }
}
