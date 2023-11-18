import Databases.DatabaseConnector;
import GUI.*;
import java.io.IOException;
import java.sql.*;

public class Main {

    public static String url = "jdbc:mysql://localhost:****/tasks";
    public static String username = "root";
    public static String password = "*******";
    public static Connection connection;

    public static void main(String[] args) throws SQLException, IOException {

        //Database connection
        DatabaseConnector connector = new DatabaseConnector(url, username, password);
        try{
            connection = connector.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            String tableName = "tasks";
            ResultSet resultSet = metaData.getTables(null, null, tableName, null);
            if (!resultSet.next()) {
                Statement statement = connection.createStatement();
                statement.executeUpdate("CREATE TABLE tasks(id INT " +
                        "PRIMARY KEY AUTO_INCREMENT, " +
                        "name VARCHAR(60) NOT NULL, importance INT NOT NULL)");
            }
        }catch (SQLException e){
            System.out.println("Connection to database failed");
            e.printStackTrace();
        }
  
        //JFrame starting
        MainWindow mainWindow = new MainWindow();
        LangOption.addLangOption(connection);
        Buttons.addButtons(mainWindow, connection);
        Link.addLink(mainWindow);

        mainWindow.setVisible(true);
        connector.close();
    }

}
