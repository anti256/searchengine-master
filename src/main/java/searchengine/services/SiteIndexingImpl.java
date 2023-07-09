package searchengine.services;

import lombok.RequiredArgsConstructor;
import model.StatusIndexing;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
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
    public JSONObject startSitesIndexing()
            throws HeuristicRollbackException, SystemException, HeuristicMixedException, RollbackException {
        JSONObject response = new JSONObject();//создание json-объекта
        if (indexingState == true){ //если индексация запущена
            //добавление строк в объект json-линии
            response.put("result", false);
            response.put("error", "Индексация уже запущена");
            return response;}
        List<Site> sitesList = sites.getSites();//заполнение list'а сайтами из файла конфигурации
        Transaction transaction = session.beginTransaction();
        new addAnotherDBrecords(sitesList);//добавление в БД записей для отработки
        ArrayList<Integer> indexArray = new ArrayList<Integer>();//список id из БД сайтов из заполненного list'а
        for (int i = 0; i < sitesList.size(); i++) {//наполнение списка с id из БД сайтов из заполненного list'а
            String indexFindQuery = "from " + model.Site.class.getSimpleName()
                    + " sites where sites.url = \'" + sitesList.get(i).getUrl() + "\'";System.out.println(indexArray);
            Query query = session.createQuery(indexFindQuery);
            List<model.Site> results = query.list();
            for (model.Site st:results) {
                indexArray.add(st.getId());
            }
        }
        System.out.println("---------------");
         for (int i = indexArray.size()-1; i > -1 ; i--) {//очистка БД по найденным id
            String hqlPages = "delete " + model.Page.class.getSimpleName() + " where siteId = " + indexArray.get(i);
            session.createQuery(hqlPages).executeUpdate();
            String hql = "delete " + model.Site.class.getSimpleName() + " where id = " + indexArray.get(i);
            System.out.println(hql);
            session.createQuery(hql).executeUpdate();
         }
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