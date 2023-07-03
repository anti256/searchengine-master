package searchengine;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@SpringBootApplication
public class Application {
    public static Session session;
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        session = SessionCreate.getInstance();
        //SessionFactoryCreate.create();//создание фабрики сессий
        //SessionFactoryCreate.close();
//        Session session = sessionFactory.openSession();
//        session.beginTransaction();
//        Query query = session.createQuery( "ALTER TABLE PAGE ADD INDEX (path(200))");
//        query.executeUpdate();
        //sessionFactory.close();

        //создание индекса по полю path
//        Connection connection;
//        try {
//            connection = DriverManager.getConnection(
//                            "jdbc:mysql://localhost:3306/search_engine?user=root&password=935117256A1B2C3D4_");
//            connection.createStatement().execute("ALTER TABLE PAGE ADD INDEX (path(200))");
//            connection.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            };



    }
}
