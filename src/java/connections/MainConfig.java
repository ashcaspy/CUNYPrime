package connections;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



/**
 *
 * @author Ashley
 */

public class MainConfig {

    public static Connection getConnection() throws URISyntaxException, SQLException, ClassNotFoundException {
        
    // Heroku connection
        /*
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        */
    // URL FOR LOCALHOST DEBUGGING
        String dbUrl = "jdbc:postgresql://ec2-107-21-219-235.compute-1.amazonaws.com:5432/dejqt9ki5rgaao?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";
        String username = "dohlisjkikwpju";
        String password = "P8E-Lh7jMSEUfyQb5RrR4m-fEJ";
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(dbUrl, username, password);
    }
}