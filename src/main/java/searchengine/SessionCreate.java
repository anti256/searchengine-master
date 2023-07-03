package searchengine;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class SessionCreate {

  private static volatile Session instance;
  //public static Transaction transaction;

  private SessionCreate(){}

  public static  Session getInstance (){
    if (instance == null) {
      synchronized (SessionCreate.class) {
        if (instance == null) {
          StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                  .configure("hibernate.cfg.xml").build();
          Metadata metadata = new MetadataSources(registry).getMetadataBuilder().build();
          SessionFactory sessionFactory = metadata.getSessionFactoryBuilder().build();
          instance = sessionFactory.openSession();
        }
      }
    }
    return instance;
    //transaction = session.beginTransaction();
    //return sessionFactory;
    //session = sessionFactory.openSession();
    //session.beginTransaction();
    //Query query = session.createQuery( "ALTER TABLE PAGE ADD INDEX (path(200))");
    //query.executeUpdate();
  }

  public static void close(){
    instance.close();
  }

}
