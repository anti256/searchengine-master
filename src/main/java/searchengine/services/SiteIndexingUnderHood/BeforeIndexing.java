package searchengine.services.SiteIndexingUnderHood;

import model.Site;
import model.StatusIndexing;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static searchengine.Application.session;

public abstract class BeforeIndexing {

    public static ResultSet selectBD(String query) {
        Connection connection;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/search_engine?user=root&password=935117256A1B2C3D4_");
            //connection.createStatement().execute("SELECT * FROM SITE WHERE STATUS = 'INDEXING'");
            ResultSet result = connection.createStatement().executeQuery(query);
            connection.close();
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        };
        return null;
    }

    public static Boolean isIndexing (){
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Site> critQuery = builder.createQuery(model.Site.class);
        Root<Site> root = critQuery.from(model.Site.class);
        critQuery.select(root).where(builder.equal(root.get("status"), StatusIndexing.INDEXING));
        Query<Site> query = session.createQuery(critQuery);
        List<Site> indexingSitesFromBD = query.getResultList();
        if (indexingSitesFromBD.size() > 0){
            return true;
        }
        return false;
    }

    public static List<model.Page> loadPagesFromBD (model.Site site){
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<model.Page> critQuery = builder.createQuery(model.Page.class);
        Root<model.Page> root = critQuery.from(model.Page.class);
        critQuery.select(root).where(builder.equal(root.get("site1"), site.getId()));
        Query<model.Page> query = session.createQuery(critQuery);
        return query.getResultList();
    }

    public static ArrayList<Site> loadSitesFromBDbyCFGorCreateNew(List<searchengine.config.Site> sitesList){
        Transaction transaction = session.beginTransaction();
        ArrayList<Site> defaultSiteList = new ArrayList<>();
        for (int i = 0; i < sitesList.size(); i++) {//наполнение списка с id из БД сайтов из заполненного list'а
            System.out.println("Наполнение списка сущностями из БД по файлу конфигурации, итерация - " + i);
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<model.Site> critQuery = builder.createQuery(model.Site.class);
            Root<model.Site> root = critQuery.from(model.Site.class);
            critQuery.select(root).where(builder.equal(root.get("url"), sitesList.get(i).getUrl()));
            Query<model.Site> query = session.createQuery(critQuery);
            List<model.Site> defaultList = query.getResultList();

            System.out.println("defaultList.size = " + defaultList.size());
            defaultSiteList.addAll(defaultList);
        }
        if (defaultSiteList.size() == 0) {
            for (int i = 0; i < sitesList.size(); i++) {
                model.Site defaultSite = new model.Site();
                defaultSite.setUrl(sitesList.get(i).getUrl());
                defaultSite.setName(sitesList.get(i).getName());
                defaultSite.setStatus(StatusIndexing.INDEXED);
                defaultSite.setStatusTime(new Date());
                session.persist(defaultSite);
                defaultSiteList.add(defaultSite);
            }
        }
        transaction.commit();
        return defaultSiteList;
    }

    public static void deleteFromBD (List<searchengine.config.Site> sitesList){
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/search_engine?user=root&password=935117256A1B2C3D4_");
        ArrayList<model.Page> pagesCfgFromBD  = new ArrayList<>();
        ArrayList<Integer> idSites = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < sitesList.size(); i++) {
            String subQuery = sitesList.get(i).getUrl().replaceAll("https://www.","");
            subQuery =subQuery.replaceAll("http://www.","");
            if (subQuery.substring(subQuery.length()-1).equals("/")){
                subQuery = subQuery.substring(0,subQuery.length()-1);
            }
            System.out.println("Начало итерации удаления");
            String query = "SELECT * FROM SITE WHERE url LIKE '%" + subQuery + "_';";
            //pagesCfgFromBD.addAll(BeforeIndexing.loadPagesFromBD(sitesCfgFromBD.get(i)));
            System.out.println("До удаления");
            /*ResultSet rs = connection.createStatement().executeQuery(query);
            while(rs.next()){
                int sID = rs.getInt("id");
                System.out.println(sID);
                idSites.add(sID);
                builder.append("DELETE FROM PAGE WHERE SITE_ID =" + sID + ";");
                //query = "DELETE FROM PAGE WHERE SITE_ID =" + sID + ";";
                //ResultSet rsDelete = connection.createStatement().executeQuery(query);
            }*/
            query = "DELETE FROM SITE WHERE url LIKE '%" + subQuery + "%';";
            connection.createStatement().executeUpdate(query);
            System.out.println("После удаления");
        }
        connection.close();
        } catch (
                SQLException e) {
            e.printStackTrace();
        };


    }
    /*System.out.println("Удаление Pages");
        for (int i = pagesCfgFromBD.size()-1; i > -1 ; i--) {
            session.remove(pagesCfgFromBD.get(i));
            session.flush();
        }*/

}

    /*
    public static ArrayList<Integer> findIDfilesFromCfg(List<Site> list) throws SQLException {
        Connection connection;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/search_engine?user=root&password=935117256A1B2C3D4_");
            //connection.createStatement().execute("SELECT * FROM SITE WHERE STATUS = 'INDEXING'");

            for (int i = 0; i < list.size(); i++) {
                String sqlDefaultQuery = "select id from site where url = \'" + list.get(i).getUrl() + "\'";
                ResultSet rs = selectBD(sqlDefaultQuery);

            }
        }
            ResultSet result = connection.createStatement().executeQuery(query);
            connection.close();
            return  result;
        } catch (SQLException e) {
            e.printStackTrace();

        return null;
    }
} catch (SQLException e) {
            throw new RuntimeException(e);
        }
       */
/*while (results.next()) {
        	Integer id = results.getInt(“id”);
        	String name = results.getString(“name”);
        	System.out.println(results.getRow() + ". " + id + "\t"+ name);
}*/


