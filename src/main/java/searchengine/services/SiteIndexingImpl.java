package searchengine.services;

import lombok.RequiredArgsConstructor;
import model.Site;
import model.StatusIndexing;
import org.hibernate.Transaction;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import searchengine.config.SitesList;
import searchengine.services.SiteIndexingUnderHood.BeforeIndexing;
import searchengine.services.SiteIndexingUnderHood.UrlListFromSite;
//import searchengine.SessionFactoryCreate;
//import model.Site;

import javax.transaction.*;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static searchengine.Application.session;

@Service
@RequiredArgsConstructor
public class SiteIndexingImpl implements SiteIndexing{
    boolean indexingState = false;
    private final SitesList sites;
    //ArrayList<model.Page> pagesCfgFromBD  = new ArrayList<>();


    @Override
    public JSONObject startSitesIndexing()
            throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException,
            SQLIntegrityConstraintViolationException {
        JSONObject response = new JSONObject();//создание json-объекта
        Transaction transaction = session.beginTransaction();
        List<searchengine.config.Site> sitesList = sites.getSites();//заполнение list'а сайтами из файла конфигурации
        if (BeforeIndexing.isIndexing()) {
            response.put("result", false);
            response.put("error", "Индексация уже запущена");
            transaction.commit();
            return response;
        }
        ArrayList<model.Site> sitesEntityFromCfg = new ArrayList<>();//сущности из БД, соответствующие сайтам из файла конфигурации
        transaction.commit();

        new addAnotherDBrecords();//добавление в БД записей для отработки

        sitesEntityFromCfg.addAll(BeforeIndexing.loadSitesFromBDbyCFGorCreateNew(sitesList));//Наполнение списка сущностями из БД по файлу конфигурации либо создание новых
        System.out.println("sitesEntityFromCfg.size = " + sitesEntityFromCfg.size());
        System.out.println("Наполнение списка сущностями из БД по файлу конфигурации закончено");
        //BeforeIndexing.deleteFromBD(sitesEntityFromCfg);//удаление всех сущностей по файлу конфигурации
        BeforeIndexing.deleteFromBD(sitesList);
       for (int i = 0; i < sitesEntityFromCfg.size(); i++) {
           transaction.begin();
           model.Site defSite = new model.Site();
           defSite.setName(sitesEntityFromCfg.get(i).getName());
           defSite.setUrl(sitesEntityFromCfg.get(i).getUrl());
           defSite.setStatus(StatusIndexing.INDEXING);
           defSite.setStatusTime(new Date());
            session.persist(defSite);
            transaction.commit();
           System.out.println("url = " + defSite.getUrl() + ", defSite.getId() - " + defSite.getId());
           ArrayList<String> pageUrl = new ArrayList<>();
          // UrlListFromSite ulfs = new UrlListFromSite(defSite);
           Boolean boo = (new UrlListFromSite(defSite)).getUrlReadyList();
           //pageUrl.addAll((new UrlListFromSite(defSite)).getUrlReadyList());
           transaction.begin();
           if (!defSite.getStatus().equals(StatusIndexing.FAILED)){
           defSite.setStatus(StatusIndexing.INDEXED);}
           transaction.commit();

        }

        response.put("result", true);
        //transaction.commit();
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