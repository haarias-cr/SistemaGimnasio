package util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Entrega SIEMPRE una nueva Connection. No cachea conexiones.
 * Cada DAO debe usar try-with-resources para cerrarla.
 */
public class DBConnection {

    public static Connection getConnection() {
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in == null) {
                throw new RuntimeException("No se encontró db.properties en el classpath.");
            }

            Properties props = new Properties();
            props.load(in);

            String host = props.getProperty("host").trim();
            String port = props.getProperty("port").trim();
            String database = props.getProperty("database").trim();
            String user = props.getProperty("user").trim();
            String password = props.getProperty("password").trim();

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";

            // Asegura el driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(url, user, password);

        } catch (Exception e) {
            throw new RuntimeException("Error abriendo conexión MySQL: " + e.getMessage(), e);
        }
    }
}
