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
        
        URI dbUri = new URI(System.getenv("DATABASE_URL"));
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        
    
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(dbUrl, username, password);
    }
}