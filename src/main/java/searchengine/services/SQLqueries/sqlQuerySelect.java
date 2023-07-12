package searchengine.services.SQLqueries;

import searchengine.config.Site;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class sqlQuerySelect {

    public static ResultSet selectBD(String query){
        Connection connection;
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/search_engine?user=root&password=935117256A1B2C3D4_");
            //connection.createStatement().execute("SELECT * FROM SITE WHERE STATUS = 'INDEXING'");
            ResultSet result = connection.createStatement().executeQuery(query);
            connection.close();
            return  result;
        } catch (SQLException e) {
            e.printStackTrace();
        };
        return null;
    }

    public static ArrayList<Integer> findIDfilesFromCfg(List<Site> list){
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
/*while (results.next()) {
        	Integer id = results.getInt(“id”);
        	String name = results.getString(“name”);
        	System.out.println(results.getRow() + ". " + id + "\t"+ name);
}*/


