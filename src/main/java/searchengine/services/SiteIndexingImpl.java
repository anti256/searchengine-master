package searchengine.services;

import lombok.RequiredArgsConstructor;
import model.StatusIndexing;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
//import searchengine.SessionFactoryCreate;
//import model.Site;

import javax.transaction.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static searchengine.Application.session;

@Service
@RequiredArgsConstructor
public class SiteIndexingImpl implements SiteIndexing{
    boolean indexingState = false;
    private final SitesList sites;
    //List<Site> sitesList = sites.getSites();



    @Override
    public JSONObject startSitesIndexing() throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException {
        JSONObject response = new JSONObject();//создание json-объекта
        if (indexingState == true){ //если индексация запущена
            //добавление строк в объект json-линии
            response.put("result", false);
            response.put("error", "Индексация уже запущена");
            return response;}
        List<Site> sitesList = sites.getSites();
        ArrayList<model.Site> dbSite = new ArrayList<>();
        Transaction transaction = session.beginTransaction();
        new addAnotherDBrecords(sitesList);
 /*       for (int i = 0; i < sitesList.size(); i++) {
            System.out.println("6" + i);
            response.put(sitesList.get(i).getUrl(),sitesList.get(i).getName());
            model.Site defaultSite = new model.Site();
            defaultSite.setUrl(sitesList.get(i).getUrl());
            defaultSite.setName(sitesList.get(i).getName());
            defaultSite.setStatus(StatusIndexing.INDEXING);
            defaultSite.setStatusTime(new Date());
            dbSite.add(defaultSite);
            System.out.println("7" + i);
            System.out.println(defaultSite.getUrl());
            System.out.println(defaultSite.getName());
            session.persist(defaultSite);
            System.out.println("8" + i);
        }*/

        //transaction.commit();
        response.put("result", true);
        //String hql = "";
//        for (int i = 0; i < sitesList.size(); i++) {
//            String hql = "delete model.Site.class.getSimpleName() where url = \'" + sitesList.get(i).getUrl() + "\'";
//            System.out.println(hql);
//            session.createQuery(hql);
//        }

//        for (int i = dbSite.size()-1; i > -1 ; i--) {
//            if (dbSite.get(i).getUrl() == sitesList.get(i).getUrl()){
//                session.delete(dbSite.get(i));//удаляет из БД
//                dbSite.remove(i);//удаляет соответствующий записи из БД экземпляр класса
//            }
//        }

//        String hql = "delete " + model.Site.class.getSimpleName() + " where url = \'https://www.site5.ru\'";
//        session.createQuery(hql).executeUpdate();
//        System.out.println("HQL :" + hql);


        transaction.commit();
        //System.out.println("dbSite.size = " + dbSite.size());
       /* for (int i = 0; i < dbSite.size(); i++) {
            System.out.println(dbSite.get(i).getName());
        }*/
        //hql = hql.substring(0,hql.length()-1);
        //System.out.println(hql);
        //session.createQuery(hql);
        return response;
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