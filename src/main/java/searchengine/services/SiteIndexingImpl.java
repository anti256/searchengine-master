package searchengine.services;

import lombok.RequiredArgsConstructor;
import model.StatusIndexing;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.services.SiteIndexingUnderHood.BeforeIndexing;
//import searchengine.SessionFactoryCreate;
//import model.Site;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.*;
import java.util.ArrayList;
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
            throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException {
        JSONObject response = new JSONObject();//создание json-объекта
        Transaction transaction = session.beginTransaction();
        List<Site> sitesList = sites.getSites();//заполнение list'а сайтами из файла конфигурации
        if (BeforeIndexing.isIndexing()) {
            response.put("result", false);
            response.put("error", "Индексация уже запущена");
            transaction.commit();
            return response;
        }
        ArrayList<model.Site> sitesCfgFromBD  = new ArrayList<>();//сущности из БД, соответствующие сайтам из файла конфигурации

        new addAnotherDBrecords();//добавление в БД записей для отработки

        sitesCfgFromBD.addAll(BeforeIndexing.loadSitesFromBDbyCFG(sitesList));//Наполнение списка сущностями из БД по файлу конфигурации
        System.out.println("sitesCfgFromBD.size = " + sitesCfgFromBD.size());
        System.out.println("Наполнение списка сущностями из БД по файлу конфигурации закончено");
        BeforeIndexing.deleteFromBD(sitesCfgFromBD);//удаление всех сущностей по файлу конфигурации





        response.put("result", true);
        transaction.commit();
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