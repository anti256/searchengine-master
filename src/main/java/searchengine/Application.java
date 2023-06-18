package searchengine;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        SessionFactory sessionFactory = SessionFactoryCreate.Create();//создание фабрики сессий
        Session session = sessionFactory.openSession();
        sessionFactory.close();

        //создание индекс по полю path
        Connection connection;
        try {
            connection = DriverManager.getConnection(
                            "jdbc:mysql://localhost:3306/search_engine?user=root&password=935117256A1B2C3D4_");
            connection.createStatement().execute("ALTER TABLE PAGE ADD INDEX (path(255))");
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
            };



    }
}
