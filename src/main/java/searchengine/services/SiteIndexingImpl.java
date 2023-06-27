package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SiteIndexingImpl implements SiteIndexing{
    boolean indexingState = false;
    private final SitesList sites;
    //List<Site> sitesList = sites.getSites();



    @Override
    public JSONObject startSitesIndexing() {
        JSONObject response = new JSONObject();//создание json-объекта
        if (indexingState == true){ //если индексация запущена
            //добавление строк в объект json-линии
            response.put("result", false);
            response.put("error", "Индексация уже запущена");
            return response;}
        List<Site> sitesList = sites.getSites();
        for (int i = 0; i < sitesList.size(); i++) {
            response.put(sitesList.get(i).getUrl(),sitesList.get(i).getName());
        }

        response.put("result", true);
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