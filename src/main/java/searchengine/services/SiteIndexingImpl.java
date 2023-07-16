package searchengine.services;

import lombok.RequiredArgsConstructor;
import model.StatusIndexing;
import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
//import searchengine.SessionFactoryCreate;
//import model.Site;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import static searchengine.Application.session;
import static searchengine.services.SQLqueries.sqlQuerySelect.selectBD;

@Service
@RequiredArgsConstructor
public class SiteIndexingImpl implements SiteIndexing{
    boolean indexingState = false;
    private final SitesList sites;
    ArrayList<model.Page> pagesCfgFromBD  = new ArrayList<>();


    @Override
    public JSONObject startSitesIndexing()
            throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException {
        JSONObject response = new JSONObject();//создание json-объекта
        Transaction transaction = session.beginTransaction();
        List<Site> sitesList = sites.getSites();//заполнение list'а сайтами из файла конфигурации
        if (isIndexing()) {
            response.put("result", false);
            response.put("error", "Индексация уже запущена");
            transaction.commit();
            return response;
        }
        ArrayList<model.Site> sitesCfgFromBD  = new ArrayList<>();//сущности из БД, соответствующие сайтам из файла конфигурации

        new addAnotherDBrecords();//добавление в БД записей для отработки

        for (int i = 0; i < sitesList.size(); i++) {//наполнение списка с id из БД сайтов из заполненного list'а
            System.out.println("Наполнение списка сущностями из БД по файлу конфигурации, итерация - " + i);

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<model.Site> critQuery = builder.createQuery(model.Site.class);
            Root<model.Site> root = critQuery.from(model.Site.class);
            critQuery.select(root).where(builder.equal(root.get("url"), sitesList.get(i).getUrl()));
            Query<model.Site> query = session.createQuery(critQuery);
            List<model.Site> defaultList = query.getResultList();

            System.out.println("defaultList.size = " + defaultList.size());
            sitesCfgFromBD.addAll(defaultList);
            System.out.println("sitesCfgFromBD.size = " + sitesCfgFromBD.size());
        }
        System.out.println("Наполнение списка сущностями из БД по файлу конфигурации закончено");
        for (int i = sitesCfgFromBD.size()-1; i > -1 ; i--) {
            System.out.println("Начало итерации удаления");
            pagesCfgFromBD.addAll(loadPagesFromBD(sitesCfgFromBD.get(i)));

            System.out.println("До удаления");
            session.remove(sitesCfgFromBD.get(i));
            //sitesCfgFromBD.remove(sitesCfgFromBD.get(i));
            session.flush();
            System.out.println("После удаления");
        }
        System.out.println("Удаление Pages");
        for (int i = pagesCfgFromBD.size()-1; i > -1 ; i--) {
            session.remove(pagesCfgFromBD.get(i));
            session.flush();
        }


        response.put("result", true);
        transaction.commit();
        return response;
    }

    private Boolean isIndexing (){
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<model.Site> critQuery = builder.createQuery(model.Site.class);
        Root<model.Site> root = critQuery.from(model.Site.class);
        critQuery.select(root).where(builder.equal(root.get("status"), StatusIndexing.INDEXING));
        Query<model.Site> query = session.createQuery(critQuery);
        List<model.Site> indexingSitesFromBD = query.getResultList();
        if (indexingSitesFromBD.size() > 0){
            return true;
        }
        return false;
    }

    private List<model.Page> loadPagesFromBD (model.Site site){
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<model.Page> critQuery = builder.createQuery(model.Page.class);
        Root<model.Page> root = critQuery.from(model.Page.class);
        critQuery.select(root).where(builder.equal(root.get("site1"), site.getId()));
        Query<model.Page> query = session.createQuery(critQuery);
        return query.getResultList();
    }

}
/*
●	В сервисе индексации сайтов пропишите код, который будет брать из конфигурации приложения список сайтов и по каждому сайту:

○	удалять все имеющиеся данные по этому сайту (записи из таблиц site и page);

○	создавать в таблице site новую запись со статусом INDEXING;

○	обходить все страницы, начиная с главной, добавлять их адреса, статусы и содержимое в базу данных в таблицу page;

○	в процессе обхода постоянно обновлять дату и время в поле status_time таблицы site на текущее;

○	по завершении обхода изменять статус (поле status) на INDEXED;

○	если произошла ошибка и обход завершить не удалось, изменять статус на FAILED и вносить в поле last_error
понятную информацию о произошедшей ошибке.


 */